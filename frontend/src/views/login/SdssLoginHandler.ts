/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import Employee from '@/dto/Employee'
import { useEmployeeStore } from '@/store/EmployeeStore'
import { Fields, SecurityConstants } from '@/constants/DpConstants'
import { publicPost } from '@/utils/GenericApiHelper.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { SdssLoginUserResponse } from '@/views/login/model/SdssLoginUserResponse.ts'
import { Paths } from '@/Paths'
import { applyEmployeeLanguage } from '@/utils/language'

class SdssLoginHandler {
  // Pre-signed dev token (Admin, GRP 696797, valid 1 year from 2026-09-25). Only used in local env.
  private readonly LOCAL_BYPASS_TOKEN = 'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZ3JwTnVtYmVyIjoiNjk2Nzk3IiwiZW1wbG95ZWVOYW1lIjoiQWRtaW4iLCJmdWxsTmFtZSI6IkFkbWluIEFkbWluIiwidXNlck5hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW5AZHViYWlwb2xpY2UuZ292LmFlIiwicm9sZXMiOlsiQURNSU4iXSwicm9sZUxpc3QiOlsiUk9MRV9BRE1JTiJdLCJsYW5ndWFnZSI6ImVuIiwiZW1wVHlwZSI6MiwiZ2VuZGVyIjoibWFsZSIsImRlcGFydG1lbnRJZCI6MSwiZGVwYXJ0bWVudE5hbWUiOiJJVCBEZXBhcnRtZW50Iiwic2VjdG9ySWQiOjEsInNlY3Rvck5hbWUiOiJJVCBTZWN0b3IiLCJyYXRpbmdzIjp0cnVlLCJkZWNpc2lvbiI6dHJ1ZSwicXVlc3Rpb25WaWV3Ijp0cnVlLCJjaGF0Q29udGVudHMiOnRydWUsInNjcm9sbGluZ1RleHQiOnRydWUsInNvY2lhbE1lZGlhRmVlZHMiOnRydWUsImludGVybmFsRG9jdW1lbnRzIjp0cnVlLCJpc0RlbHBoaU1vZGVyYXRvciI6ZmFsc2UsImlzcyI6IkRFRkFVTFRfSVNTVUVSIiwiaWF0IjoxNzkwMzUzNjk0LCJleHAiOjE4MjE4ODk2OTR9.3yrfVjzOewo5Jxrlislz2Pn9rt_PpDJNYJKdrT44VdROiN42Reygu8J_kWuGlLXn5tXS5zxOcJuPDdXBHLfkdQ'

  async sdssLogin(username: string, password: string, grpNumber: number | null, router: any, systemRoles?: string[]) {
    console.log(
      'Attempting login with username:',
      username,
      'and grpNumber:',
      grpNumber,
      'and systemRoles:',
      systemRoles
    )
    const userLanguage = localStorage.getItem('selectedLanguage') ?? 'ar'
    const isLocalEnvironment = (Paths.NODE_ENV ?? '').toLowerCase().includes('local')
    const useRolesLocalLogin = Paths.USE_ROLES_LOCAL_LOGIN === 'true'

    if (isLocalEnvironment) {
      this.applyLocalBypass(router, userLanguage)
      return
    }

    const payload: {
      username: string
      password: string
      grpNumber: number | null
      systemRoles?: string[]
    } = {
      username: username,
      password: password,
      grpNumber: grpNumber
    }

    if (useRolesLocalLogin) {
      payload.systemRoles = systemRoles ?? []
    }

    // Make an API call to log in
    publicPost<SuccessResponse<SdssLoginUserResponse>>(Paths.BASE_LOGIN_URI, payload, userLanguage)
      .then((response: SuccessResponse<SdssLoginUserResponse>) => {
        console.log(response)
        if (response?.success) {
          const employeeStore = useEmployeeStore()
          const loginUserResponse = response.data

          // Store employee data and token in localStorage
          localStorage.setItem(SecurityConstants.TOKEN, loginUserResponse.token)
          const employee: Employee = this.updateEmployee(loginUserResponse)
          localStorage.setItem(Fields.LOGGED_IN_EMP, JSON.stringify(employee))
          localStorage.setItem(Fields.EMPLOYEE, JSON.stringify(employee))

          if (typeof employee.language === 'string') {
            localStorage.setItem(Fields.EMPLOYEE_LANG, employee.language)
          }
          if (typeof employee.language === 'string') {
            localStorage.setItem(Fields.EMPLOYEE_LANG_LOCAL_STORAGE, employee.language)
          }
          applyEmployeeLanguage(employee)

          // Update the store with the employee data
          employeeStore.updateEmployee(employee)

          location.reload()

          // Navigate to the home page
          router.push({ name: 'home' }) // Redirect to home page after successful login
        } else {
          console.log('Login failed', response)
        }
      })
      .catch((error) => {
        console.error('Login error:', error)
      })
  }

  updateEmployee(user: SdssLoginUserResponse): Employee {
    const employee = new Employee()

    employee.name = user.fullName
    employee.id = user.grpNumber
    employee.empGrp = user.grpNumber
    employee.grpNumber = user.grpNumber
    employee.empGrpNumber = user.grpNumber
    employee.email = user.email
    employee.gender = user.gender
    employee.empType = user.empType
    employee.departmentName = user.departmentName
    employee.departmentId = user.departmentId
    employee.exp = user.exp
    employee.roles = user.roles
    employee.language = user.languageCode
    employee.languageName = user.language
    employee.internalDocuments = user.internalDocuments
    employee.chatContents = user.chatContents
    employee.socialMediaFeeds = user.socialMediaFeeds
    employee.decision = user.decision
    employee.questionView = user.questionView
    employee.ratings = user.ratings
    employee.scrollingText = user.scrollingText
    employee.isDelphiModerator = user.isDelphiModerator
    employee.permissionList = [] // Assuming permissions are not in the response, keeping it empty
    employee.slmMilitaryFName = user.employeeName // Assuming it maps to employeeName
    employee.userPhoto = user.userPhoto

    console.log(employee.roles)
    return employee
  }

  private applyLocalBypass(router: any, userLanguage: string) {
    const employeeStore = useEmployeeStore()
    const token = this.LOCAL_BYPASS_TOKEN
    const mockResponse: SdssLoginUserResponse = {
      token,
      grpNumber: 696797,
      employeeName: 'Admin',
      fullName: 'Admin Admin',
      userName: 'admin',
      email: 'admin@dubaipolice.gov.ae',
      roles: ['ADMIN'],
      language: userLanguage,
      languageCode: 'en',
      empType: 2,
      gender: 'male',
      departmentId: 1,
      departmentName: 'IT Department',
      sectorId: 1,
      sectorName: 'IT Sector',
      ratings: true,
      decision: true,
      questionView: true,
      chatContents: true,
      scrollingText: true,
      socialMediaFeeds: true,
      internalDocuments: true,
      isDelphiModerator: false,
      exp: 1821889694,
      iat: 1790353694,
    } as any

    localStorage.setItem(SecurityConstants.TOKEN, token)
    const employee = this.updateEmployee(mockResponse)
    localStorage.setItem(Fields.LOGGED_IN_EMP, JSON.stringify(employee))
    localStorage.setItem(Fields.EMPLOYEE, JSON.stringify(employee))
    localStorage.setItem(Fields.EMPLOYEE_LANG, 'en')
    localStorage.setItem(Fields.EMPLOYEE_LANG_LOCAL_STORAGE, 'en')
    employeeStore.updateEmployee(employee)
    location.reload()
    router.push({ name: 'home' })
  }
}

export default new SdssLoginHandler()
