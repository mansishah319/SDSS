import { AsgardeoSPAClient } from '@asgardeo/auth-spa'
import axios from 'axios'
import jwt_decode from 'jwt-decode'
import { Paths } from '@/Paths'
import { Fields, SecurityConstants, URIS } from '@/constants/DpConstants'
import Employee from '@/dto/Employee'
import { useEmployeeStore } from '@/store/EmployeeStore'
import { MainSystemRolesKeys, normalizeTokenRoles } from '@/dto/enums/SystemRoles'
import { applyEmployeeLanguage } from '@/utils/language'
import { clearAuthSession } from '@/utils/authSession'
import type { SuccessResponse } from '@/dto/generic/SuccessResponse'

export const WSO2_AUTH_REDIRECT_KEY = 'sdss_wso2_redirect'

const LOCAL_ENV_PREFIX = 'local'
const TOKEN_REFRESH_BUFFER_SECONDS = 60
const auth = AsgardeoSPAClient.getInstance()

const HARDCODED_SDSS_TOKEN =
  'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZ3JwTnVtYmVyIjoiNjk2Nzk3IiwiZW1wbG95ZWVOYW1lIjoiQWRtaW4iLCJmdWxsTmFtZSI6IkFkbWluIEFkbWluIiwidXNlck5hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW5AZHViYWlwb2xpY2UuZ292LmFlIiwicm9sZXMiOlsiUE9SVEFMQURNSU4iXSwicm9sZUxpc3QiOlsiUk9MRV9QT1JUQUxBRE1JTiJdLCJsYW5ndWFnZSI6ImVuIiwiZW1wVHlwZSI6MiwiZ2VuZGVyIjoibWFsZSIsImRlcGFydG1lbnRJZCI6MSwiZGVwYXJ0bWVudE5hbWUiOiJJVCBEZXBhcnRtZW50Iiwic2VjdG9ySWQiOjEsInNlY3Rvck5hbWUiOiJJVCBTZWN0b3IiLCJyYXRpbmdzIjp0cnVlLCJkZWNpc2lvbiI6dHJ1ZSwicXVlc3Rpb25WaWV3Ijp0cnVlLCJjaGF0Q29udGVudHMiOnRydWUsInNjcm9sbGluZ1RleHQiOnRydWUsInNvY2lhbE1lZGlhRmVlZHMiOnRydWUsImludGVybmFsRG9jdW1lbnRzIjp0cnVlLCJpc0RlbHBoaU1vZGVyYXRvciI6ZmFsc2UsImlzcyI6IkRFRkFVTFRfSVNTVUVSIiwiaWF0IjoxNzkwMzU5NTIxLCJleHAiOjE4ODQ5Njc1MjF9.9__CnJO6gZSX29cXt9reYTq2JSiovaLV600uPQvvHG6rCfiUel4GC3M-FkI7QvT3MjFkafRhCVdNP9apn9L2PQ'

let initializationPromise: Promise<unknown> | null = null
let loginPromise: Promise<void> | null = null
let refreshPromise: Promise<string | null> | null = null
let refreshTimeoutId: ReturnType<typeof globalThis.setTimeout> | null = null

type UserAuthoritiesResponse = {
  roles?: string[]
  permissions?: string[]
  fullName?: string | null
  language?: string | null
  internalDocuments?: boolean | null
  chatContents?: boolean | null
  socialMediaFeeds?: boolean | null
  decision?: boolean | null
  questionView?: boolean | null
  ratings?: boolean | null
  scrollingText?: boolean | null
  gradeName?: string | null
  iss?: string | null
  phoneNumber?: string | null
  userPhoto?: string | null
}

const trimTrailingSlash = (value: string | undefined): string => (value ?? '').replace(/\/+$/, '')

const getCoreApiUrl = (): string => {
  const coreUrl = trimTrailingSlash(Paths.VITE_CORE_SERVER_URL)
  return coreUrl.endsWith('/v1') ? coreUrl : `${coreUrl}/v1`
}

const getServicePermissionName = (): string => Paths.VITE_SERVICE_PERMISSION_NAME || 'SDSSService'

const getIdsTokenUrl = (): string => `${getCoreApiUrl()}/idsAuth/getIdsToken/${getServicePermissionName()}`

const getRefreshTokenUrl = (): string => `${getCoreApiUrl()}/idsAuth/refreshIdsToken/${getServicePermissionName()}`

const isLocalEnvironment = (): boolean => (Paths.NODE_ENV ?? '').toLowerCase().startsWith(LOCAL_ENV_PREFIX)

console.log('[SDSS] NODE_ENV:', Paths.NODE_ENV)

console.log('[SDSS] isLocalEnvironment:', isLocalEnvironment())

const initialize = async () => {
  if (isLocalEnvironment()) {
    initializeHardcodedToken()
    return null
  }

  if (!auth) {
    throw new Error('The Asgardeo SPA client is unavailable.')
  }

  const missingConfig = [
    ['VITE_SIGNIN_REDIRECT_URL', Paths.VITE_SIGNIN_REDIRECT_URL],
    ['VITE_SIGNEDOUT_REDIRECT_URL', Paths.VITE_SIGNEDOUT_REDIRECT_URL],
    ['VITE_CLIENT_ID', Paths.VITE_CLIENT_ID],
    ['VITE_CLIENT_SECRET', Paths.VITE_CLIENT_SECRET],
    ['VITE_IDS_BASE_URL', Paths.VITE_IDS_BASE_URL],
  ].filter(([, value]) => !value)

  if (missingConfig.length > 0) {
    throw new Error(`Missing WSO2 configuration: ${missingConfig.map(([name]) => name).join(', ')}`)
  }

  const client = auth

  initializationPromise ??= client.initialize({
    signInRedirectURL: Paths.VITE_SIGNIN_REDIRECT_URL,
    signOutRedirectURL: Paths.VITE_SIGNEDOUT_REDIRECT_URL,
    clientID: Paths.VITE_CLIENT_ID,
    clientSecret: Paths.VITE_CLIENT_SECRET,
    baseUrl: Paths.VITE_IDS_BASE_URL,
    validateIDToken: false,
  })

  await initializationPromise
  await client.updateConfig({
    signInRedirectURL: Paths.VITE_SIGNIN_REDIRECT_URL,
    signOutRedirectURL: Paths.VITE_SIGNEDOUT_REDIRECT_URL,
  })
  return client
}

const decodeToken = (token: string): any | null => {
  try {
    return jwt_decode(token)
  } catch (error) {
    console.error('Unable to decode the WSO2 token:', error)
    return null
  }
}

const clearRefreshTimer = () => {
  if (refreshTimeoutId !== null) {
    globalThis.clearTimeout(refreshTimeoutId)
    refreshTimeoutId = null
  }
}

const clearSession = () => {
  clearRefreshTimer()
  clearAuthSession()
}

const saveEmployee = (employee: Employee): Employee => {
  localStorage.setItem(Fields.EMPLOYEE, JSON.stringify(employee))
  localStorage.setItem(Fields.LOGGED_IN_EMP, JSON.stringify(employee))
  if (employee.language) {
    localStorage.setItem(Fields.EMPLOYEE_LANG, employee.language)
    localStorage.setItem(Fields.EMPLOYEE_LANG_LOCAL_STORAGE, employee.language)
  }
  applyEmployeeLanguage(employee)
  useEmployeeStore().updateEmployee(employee)
  return employee
}

const readStoredEmployee = (): Employee | null => {
  try {
    const employeeJson = localStorage.getItem(Fields.EMPLOYEE) || localStorage.getItem(Fields.LOGGED_IN_EMP)
    return employeeJson ? (JSON.parse(employeeJson) as Employee) : null
  } catch (error) {
    console.error('Unable to read stored employee:', error)
    return null
  }
}

const saveEmployeeFromToken = (): Employee | null => {
  const token = localStorage.getItem(SecurityConstants.TOKEN)
  if (!token) return null

  const jwt = decodeToken(token)
  if (!jwt) return null

  const employee = new Employee()
  const tokenRoles = Array.isArray(jwt.roles)
    ? jwt.roles.filter((role: unknown): role is string => typeof role === 'string')
    : Array.isArray(jwt.roleList)
      ? jwt.roleList.filter((role: unknown): role is string => typeof role === 'string')
      : Array.isArray(jwt.role)
        ? jwt.role.filter((role: unknown): role is string => typeof role === 'string')
        : []
  const normalizedRoles = normalizeTokenRoles(tokenRoles)

  employee.name = jwt.fullName ?? jwt.employeeName ?? jwt.ntLogin ?? jwt.sub ?? null
  employee.userName = jwt.userName ?? jwt.ntLogin ?? null
  employee.encId = jwt.sub ?? null
  employee.id = jwt.grpNumber == null ? null : Number(jwt.grpNumber)
  employee.empGrp = employee.id
  employee.grpNumber = employee.id
  employee.empGrpNumber = employee.id
  employee.email = jwt.email ?? null
  employee.gender = jwt.gender ?? null
  employee.slmMilitaryFName = jwt.empType === 1 ? Fields.MILITARY : Fields.CIVILIAN
  employee.empType = jwt.empType ?? null
  employee.departmentName = jwt.departmentName ?? null
  employee.departmentId = jwt.departmentId ?? null
  employee.sectorName = jwt.sectorName ?? null
  employee.sectorId = jwt.sectorId ?? null
  employee.realm = jwt.realm ?? null
  employee.issuedAt = jwt.iat == null ? null : Number(jwt.iat)
  employee.issuer = jwt.iss ?? null
  employee.exp = jwt.exp == null ? null : Number(jwt.exp)
  employee.permissionList = Array.isArray(jwt.permissionList) ? jwt.permissionList : []
  employee.tokenRoles = tokenRoles
  employee.roles = normalizedRoles
  employee.language = jwt.languageCode ?? jwt.language ?? null
  employee.languageName = jwt.language ?? null
  employee.internalDocuments = jwt.internalDocuments ?? null
  employee.chatContents = jwt.chatContents ?? null
  employee.socialMediaFeeds = jwt.socialMediaFeeds ?? null
  employee.decision = jwt.decision ?? null
  employee.questionView = jwt.questionView ?? null
  employee.ratings = jwt.ratings ?? null
  employee.scrollingText = jwt.scrollingText ?? null
  employee.isDelphiModerator = jwt.isDelphiModerator ?? normalizedRoles.includes(MainSystemRolesKeys._MODERATOR)
  employee.gradeName = jwt.gradeName ?? null
  employee.phoneNumber = jwt.phoneNumber ?? null
  employee.guid = jwt.guid ?? null

  return saveEmployee(employee)
}

const hydrateEmployeeFromAuthorities = async (
  baseEmployee: Employee | null = readStoredEmployee(),
): Promise<Employee | null> => {
  const token = getToken()
  if (!token || !baseEmployee) return baseEmployee

  try {
    const response = await axios.get<SuccessResponse<UserAuthoritiesResponse>>(
      `${Paths.BASE_URL}${URIS.SECURITY.AUTHORITIES_URI}`,
      {
        headers: {
          Authorization: `${SecurityConstants.BEARER}${token}`,
        },
      },
    )

    const authorities = response.data?.data
    if (!response.data?.success || !authorities) return baseEmployee

    const roles = Array.isArray(authorities.roles) ? authorities.roles : []
    const permissions = Array.isArray(authorities.permissions) ? authorities.permissions : []
    const enrichedEmployee = Object.assign(new Employee(), baseEmployee)

    enrichedEmployee.name = authorities.fullName || enrichedEmployee.name
    enrichedEmployee.roles = normalizeTokenRoles(roles)
    enrichedEmployee.tokenRoles = roles
    ;(enrichedEmployee as any).permissionList = permissions
    enrichedEmployee.language = authorities.language || enrichedEmployee.language
    enrichedEmployee.languageName = authorities.language || enrichedEmployee.languageName
    enrichedEmployee.internalDocuments = authorities.internalDocuments ?? enrichedEmployee.internalDocuments
    enrichedEmployee.chatContents = authorities.chatContents ?? enrichedEmployee.chatContents
    enrichedEmployee.socialMediaFeeds = authorities.socialMediaFeeds ?? enrichedEmployee.socialMediaFeeds
    enrichedEmployee.decision = authorities.decision ?? enrichedEmployee.decision
    enrichedEmployee.questionView = authorities.questionView ?? enrichedEmployee.questionView
    enrichedEmployee.ratings = authorities.ratings ?? enrichedEmployee.ratings
    enrichedEmployee.scrollingText = authorities.scrollingText ?? enrichedEmployee.scrollingText
    enrichedEmployee.gradeName = authorities.gradeName ?? enrichedEmployee.gradeName
    enrichedEmployee.phoneNumber = authorities.phoneNumber ?? enrichedEmployee.phoneNumber
    enrichedEmployee.issuer = authorities.iss ?? enrichedEmployee.issuer
    ;(enrichedEmployee as any).userPhoto = authorities.userPhoto ?? enrichedEmployee.userPhoto
    enrichedEmployee.isDelphiModerator =
      enrichedEmployee.isDelphiModerator ?? enrichedEmployee.roles.includes(MainSystemRolesKeys._MODERATOR)

    return saveEmployee(enrichedEmployee)
  } catch (error: any) {
    const status = error?.response?.status
    console.error('Unable to hydrate SDSS user permissions:', error)
    if (status === 401 || status === 403 || status === 404) {
      clearSession()
    }
    return null
  }
}

const getToken = (): string | null => {
  return localStorage.getItem(SecurityConstants.TOKEN)
}

const initializeHardcodedToken = (): string => {
  localStorage.setItem(SecurityConstants.TOKEN, HARDCODED_SDSS_TOKEN)

  console.log('[SDSS] Hardcoded token initialized')

  console.log('[SDSS] Token exists:', !!localStorage.getItem(SecurityConstants.TOKEN))

  return HARDCODED_SDSS_TOKEN
}

const isTokenValid = (token: string | null = getToken()): boolean => {
  if (!token) return false

  const jwt = decodeToken(token)
  if (!jwt?.exp) return false

  return Number(jwt.exp) - TOKEN_REFRESH_BUFFER_SECONDS > Math.floor(Date.now() / 1000)
}

const scheduleTokenRefresh = (token: string | null = getToken()) => {
  clearRefreshTimer()
  if (isLocalEnvironment() || !token) return

  const jwt = decodeToken(token)
  if (!jwt?.exp) return

  const delay = Math.max((Number(jwt.exp) - TOKEN_REFRESH_BUFFER_SECONDS) * 1000 - Date.now(), 0)
  refreshTimeoutId = globalThis.setTimeout(() => {
    void ensureValidToken(true)
  }, delay)
}

const login = async (): Promise<void> => {
  // ============================================================
  // LOCAL DEVELOPMENT
  // BYPASS WSO2 / ASGARDEO
  // ============================================================

  if (isLocalEnvironment()) {
    initializeHardcodedToken()

    console.log('[SDSS] Local environment detected.')

    console.log('[SDSS] WSO2 login bypassed.')

    return
  }

  // ============================================================
  // NORMAL WSO2 LOGIN
  // ============================================================

  if (loginPromise) {
    return loginPromise
  }

  loginPromise = (async () => {
    try {
      const client = await initialize()

      await client?.signIn()
    } catch (error) {
      console.error('WSO2 login failed:', error)
    } finally {
      loginPromise = null
    }
  })()

  return loginPromise
}

const isSignedIn = async (): Promise<boolean> => {
  try {
    const client = await initialize()
    return client ? Boolean(await client.isAuthenticated()) : false
  } catch (error) {
    console.error('Unable to check WSO2 authentication:', error)
    return false
  }
}

const handleCallback = async (): Promise<boolean> => {
  if (isLocalEnvironment()) {
    initializeHardcodedToken()

    await hydrateEmployeeFromAuthorities(saveEmployeeFromToken())

    return true
  }

  try {
    const client = await initialize()

    if (!client) return false

    await client.signIn({
      callOnlyOnRedirect: true,
    })

    if (!(await client.isAuthenticated())) {
      return false
    }

    const accessToken = await client.getAccessToken()

    if (!accessToken) {
      return false
    }

    const response = await axios.get(getIdsTokenUrl(), {
      params: {
        code: accessToken,
      },
    })

    const token = typeof response.data === 'string' ? response.data.trim() : ''

    if (!token) {
      throw new Error('The IDS token endpoint returned an empty token.')
    }

    localStorage.setItem(SecurityConstants.TOKEN, token)

    await hydrateEmployeeFromAuthorities(saveEmployeeFromToken())

    scheduleTokenRefresh(token)

    return true
  } catch (error) {
    console.error('WSO2 callback failed:', error)

    clearSession()

    return false
  }
}

const ensureValidToken = async (forceRefresh = false): Promise<string | null> => {
  const token = getToken()
  if (!token) return null
  if (isLocalEnvironment()) return token

  if (!forceRefresh && isTokenValid(token)) {
    scheduleTokenRefresh(token)
    return token
  }

  if (refreshPromise) return refreshPromise

  refreshPromise = (async () => {
    try {
      const response = await axios.post(getRefreshTokenUrl(), token, {
        headers: { 'Content-Type': 'text/plain' },
      })
      const refreshedToken = typeof response.data === 'string' ? response.data.trim() : ''
      if (!refreshedToken) throw new Error('The IDS refresh endpoint returned an empty token.')

      localStorage.setItem(SecurityConstants.TOKEN, refreshedToken)
      await hydrateEmployeeFromAuthorities(saveEmployeeFromToken())
      scheduleTokenRefresh(refreshedToken)
      return refreshedToken
    } catch (error) {
      console.error('WSO2 token refresh failed:', error)
      clearSession()
      return null
    } finally {
      refreshPromise = null
    }
  })()

  return refreshPromise
}

const logout = async (): Promise<void> => {
  clearSession()
  if (isLocalEnvironment()) return

  try {
    const client = await initialize()
    await client?.signOut()
  } catch (error) {
    console.error('WSO2 logout failed:', error)
  }
}

export default {
  clearSession,
  ensureValidToken,
  getToken,
  handleCallback,
  isLocalEnvironment,
  isSignedIn,
  isTokenValid,
  loadEmployeeFromToken: saveEmployeeFromToken,
  hydrateEmployeeFromAuthorities,
  login,
  logout,
  scheduleTokenRefresh,
}
