/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// Utility function to check if a user has any of the specified roles
import { MainSystemRolesKeys, normalizeTokenRoles } from '@/dto/enums/SystemRoles.ts'
import { Fields, SecurityConstants } from '@/constants/DpConstants'
import Employee from '@/dto/Employee.ts'

import jwt_decode from 'jwt-decode'

const readStoredEmployee = (): Employee | null => {
  const employeeJson: string | null = localStorage.getItem(Fields.EMPLOYEE)
  if (!employeeJson) return null

  try {
    return JSON.parse(employeeJson) as Employee
  } catch (e) {
    console.error('Failed to parse employee data from localStorage:', e)
    return null
  }
}

export const getEmployeeData = (): Employee | null => {
  // First try to get from token
  const token: string | null = localStorage.getItem(SecurityConstants.TOKEN)
  if (token) {
    try {
      const jwt: any = jwt_decode(token)
      const employee = new Employee()
      const tokenRoles = Array.isArray(jwt.roles) ? jwt.roles : []
      const normalizedRoles = normalizeTokenRoles(tokenRoles)
      employee.name = jwt.fullName ?? jwt.employeeName ?? null
      employee.userName = jwt.userName ?? jwt.ntLogin ?? null
      employee.encId = jwt.sub ?? null
      const grpNumber = jwt.grpNumber == null ? null : Number(jwt.grpNumber)
      employee.id = grpNumber
      employee.empGrp = grpNumber
      employee.grpNumber = grpNumber
      employee.email = jwt.email ?? null
      employee.empGrpNumber = grpNumber
      employee.gender = jwt.gender ?? null
      employee.slmMilitaryFName = jwt.employeeName ?? null
      employee.empType = jwt.empType ?? null
      employee.departmentName = jwt.departmentName ?? null
      employee.departmentId = jwt.departmentId ?? null
      employee.sectorName = jwt.sectorName ?? null
      employee.sectorId = jwt.sectorId ?? null
      employee.realm = jwt.realm ?? null
      employee.issuedAt = jwt.iat ?? null
      employee.issuer = jwt.iss ?? null
      employee.exp = jwt.exp ?? null
      employee.permissionList = jwt.permissionList ?? []
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
      // Optionally map extra fields for future use
      employee.gradeName = jwt.gradeName ?? null
      employee.phoneNumber = jwt.phoneNumber ?? null
      employee.guid = jwt.guid ?? null

      const storedEmployee = readStoredEmployee()
      if (storedEmployee) {
        return Object.assign(employee, storedEmployee)
      }

      return employee
    } catch (e) {
      console.error('Failed to decode JWT:', e)
    }
  }

  // Fall back to localStorage if token doesn't exist or decoding fails
  const storedEmployee = readStoredEmployee()
  if (storedEmployee) return storedEmployee

  return null
}

const hasRole = (rolesToCheck: MainSystemRolesKeys[]): boolean => {
  const employee = getEmployeeData()
  if (employee) {
    const roles = employee?.roles ?? []
    return rolesToCheck.some((role) => Array.from(roles).includes(role))
  }
  return false
}

// Export specific role check functions (unchanged)
export const isAdminOrModerator = (): boolean =>
  hasRole([MainSystemRolesKeys._ADMIN, MainSystemRolesKeys._COMMANDER, MainSystemRolesKeys._MODERATOR])

export const isAdmin = (): boolean => hasRole([MainSystemRolesKeys._ADMIN, MainSystemRolesKeys._COMMANDER])

export const isModeratorOnly = (): boolean => hasRole([MainSystemRolesKeys._MODERATOR])

export const isAdminOrModeratorOrValidator = (): boolean =>
  hasRole([
    MainSystemRolesKeys._ADMIN,
    MainSystemRolesKeys._COMMANDER,
    MainSystemRolesKeys._MODERATOR,
    MainSystemRolesKeys._VALIDATOR,
  ])

export const isCommanderOnly = (): boolean => hasRole([MainSystemRolesKeys._COMMANDER])
export const isValidatorOnly = (): boolean => hasRole([MainSystemRolesKeys._VALIDATOR])
export const isHasAnyRole = (): boolean =>
  hasRole([
    MainSystemRolesKeys._ADMIN,
    MainSystemRolesKeys._COMMANDER,
    MainSystemRolesKeys._MODERATOR,
    MainSystemRolesKeys._MEMBER,
    MainSystemRolesKeys._VALIDATOR,
    MainSystemRolesKeys._GUEST,
  ])

export const hasAnyExceptCommanderRole = (): boolean =>
  hasRole([
    MainSystemRolesKeys._ADMIN,
    MainSystemRolesKeys._MODERATOR,
    MainSystemRolesKeys._MEMBER,
    MainSystemRolesKeys._VALIDATOR,
  ])

export const isDelphiModerator = (): boolean => {
  const employee = getEmployeeData()
  return !!employee?.isDelphiModerator
}
