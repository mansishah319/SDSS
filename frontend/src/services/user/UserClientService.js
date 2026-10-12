/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { Paths } from '@/Paths'
import {
  create,
  downloadExcelFile,
  getAll,
  getAllWithPagination,
  getByPathVariableId,
  getImageByPathVariableId,
  remove,
  retrieveDefaultUserLanguage,
  search,
  update,
  updateLanguage
} from '@/utils/api-utils'
import { Characters, URIS } from '@/constants/DpConstants'

export default {
  search: async (requestBody, pageNumber, pageSize, sortField, sortDirection) => {
    return search(
      Paths.BASE_URL + URIS.USER.USER_SEARCH_URI,
      requestBody,
      pageNumber,
      pageSize,
      sortField,
      sortDirection
    )
  },
  updateLanguage: async (language) => {
    return updateLanguage(Paths.BASE_URL + URIS.USER.USER_UPDATE_LANG, language)
  },
  retrieveDefaultUserLanguage: async () => {
    return await retrieveDefaultUserLanguage(Paths.BASE_URL + URIS.USER.USER_RETRIEVE_LANG)
      .then((response) => {
        return response.data.data // Return the data here
      })
      .catch((error) => {
        console.log(error)
        if (error.response.status === 403) {
          // Handle permission error
          console.log(error)
        }
        throw new Error('Error retrieving default user language:', error) // Throw error to handle it correctly
      })
  },

  // Then your original code will work
  downloadExcelFile: async (requestBody, pageNumber, pageSize, sortField, sortDirection) => {
    return downloadExcelFile(
      Paths.BASE_URL + URIS.USER.USER_EXCEL_DOWNLOAD_URI,
      requestBody,
      pageNumber,
      pageSize,
      sortField,
      sortDirection
    )
  },

  getAll: async () => {
    return await getAll(Paths.BASE_URL + URIS.USER.BASE_USER_URI)
  },

  getAllWithPagination: async (pageNumber, pageSize, sortField, sortDirection) => {
    return await getAllWithPagination(
      Paths.BASE_URL + URIS.USER.USER_LIST_PAGEABLE_API,
      pageNumber,
      pageSize,
      sortField,
      sortDirection
    )
  },

  userCreation: async (formDataInstance) => {
    return await create(Paths.BASE_URL + URIS.USER.BASE_USER_URI, formDataInstance)
  },

  getByUserByGrp: async (grpNumber) => {
    return await getByPathVariableId(Paths.BASE_URL + URIS.USER.BASE_USER_URI + Characters.SLASH + grpNumber)
  },

  getUserProfileImageByGrpNumber: async (grpNumber) => {
    return await getImageByPathVariableId(
      Paths.BASE_URL + URIS.EMPLOYEE_CORE_PROFILE.BASE + URIS.EMPLOYEE_CORE_PROFILE.IMG + Characters.SLASH + grpNumber
    )
  },

  updateUser: async (requestBody) => {
    return await update(Paths.BASE_URL + URIS.USER.BASE_USER_URI, requestBody)
  },

  removeUser: async (grpNumber) => {
    return await remove(Paths.BASE_URL + URIS.USER.DELETE_USER_URI + Characters.SLASH + grpNumber)
  }
}
