/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import axios from 'axios'
import { Paths } from '@/Paths'
import jwt_decode from 'jwt-decode'
import JWT from '@/dto/JWT'
import Employee from '@/dto/Employee'
import { useEmployeeStore } from '@/store/EmployeeStore'
import router from '@/router'
import { Codes, Fields, SecurityConstants, SuccessMessages, URIS } from '@/constants/DpConstants'
import { applyEmployeeLanguage } from '@/utils/language'

class AuthService {
  async ldapLogin(next: any) {
    const axiosConfig =
      {
        withCredentials: true,
        'WWW-Authenticate': 'NTLM'
      } + Paths.SERVICE_PERMISSION_NAME
    return await axios
      .get(Paths.AUTH_PATH + URIS.SECURITY.AUTH_URI, axiosConfig)
      .then(async (response: any) => {
        const token = response.data
        localStorage.setItem(SecurityConstants.TOKEN, token)

        const employee = this.updateEmployee()
        await this.fillPermissionArr(employee, token)

        // debugger
        localStorage.setItem(Fields.LOGGED_IN_EMP, JSON.stringify(employee))
        localStorage.setItem(Fields.EMPLOYEE, JSON.stringify(employee))
        if (employee.language !== null) {
          localStorage.setItem(Fields.EMPLOYEE_LANG, employee.language)
          localStorage.setItem(Fields.EMPLOYEE_LANG_LOCAL_STORAGE, employee.language)
        }
        applyEmployeeLanguage(employee)

        // Update the store
        const employeeStore = useEmployeeStore()
        employeeStore.updateEmployee(employee)

        window.location.reload()
        if (next) next()
      })
      .catch((error: any) => {
        console.log(error)
        router.push(Codes.NOT_FOUND_ERR)
      })
  }

  async fillPermissionArr(employee: Employee, token: string) {
    // Fetch user authorities
    const authoritiesResponse = await axios.get(Paths.BASE_URL + URIS.SECURITY.AUTHORITIES_URI, {
      headers: {
        Authorization: `${SecurityConstants.BEARER}${token}`
      }
    })

    if (authoritiesResponse.data.success && authoritiesResponse.data) {
      const {
        roles,
        permissions,
        language,
        internalDocuments,
        chatContents,
        socialMediaFeeds,
        decision,
        questionView,
        ratings,
        scrollingText
      } = authoritiesResponse.data.data
      if (roles && roles.length > 0) {
        employee.roles.push(...roles)
      }

      if (permissions && permissions.length > 0) {
        employee.permissionList.push(...(permissions as []))
      }
      employee.language = language
      employee.internalDocuments = internalDocuments
      employee.chatContents = chatContents
      employee.socialMediaFeeds = socialMediaFeeds
      employee.decision = decision
      employee.questionView = questionView
      employee.ratings = ratings
      employee.scrollingText = scrollingText
    }
    console.log(SuccessMessages.USER_AUTHORITIES, employee.roles)
  }

  updateEmployee() {
    const token: string | null = localStorage.getItem(SecurityConstants.TOKEN)
    const employee = new Employee()
    let jwt = new JWT()

    if (token) {
      jwt = jwt_decode(token)

      employee.name = jwt.employeeName
      employee.encId = jwt.sub
      employee.id = jwt.grpNumber
      employee.empGrp = jwt.grpNumber
      employee.gender = jwt.gender
      employee.slmMilitaryFName = jwt.empType === 1 ? Fields.MILITARY : Fields.CIVILIAN
      employee.empType = jwt.empType
      employee.departmentName = jwt.departmentName
      employee.departmentId = jwt.departmentId
      employee.exp = jwt.exp
      employee.permissionList = jwt.permissionList
      employee.roles = []
      employee.language = null
      employee.isDelphiModerator = jwt.isDelphiModerator ?? null
    }
    return employee
  }
}

export default new AuthService()
