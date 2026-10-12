/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import UserClientService from '@/services/user/UserClientService'
import { resolveLocalizedFieldValue } from '@/utils/localizationUtils'

export default function userSearch(dataTableRequest, totalItems, setLoading, records, locale = 'en') {
  setLoading(true)
  let sortBy = dataTableRequest.value.sortBy
  const pageSize = dataTableRequest.value.pageSize
  const currentPage = dataTableRequest.value.currentPage
  const data = dataTableRequest.value.data
  const sortDesc = dataTableRequest.value.sortDesc
  if (sortBy === null || sortBy === '' || sortBy === undefined) {
    sortBy = 'updatedAt'
  }
  return UserClientService.search(data, currentPage, pageSize, sortBy, sortDesc ? 'desc' : 'asc')
    .then((response) => {
      records.value = Array.from(response.data)
      records.value.forEach((user) => {
        if (user.language && user.roles.length !== 0) {
          user.roles = user.roles
            .map((role) =>
              resolveLocalizedFieldValue({
                locale,
                item: role,
                fallback: role.role || role.nameEn || role.nameAr || ''
              })
            )
            .filter(Boolean)
            .join(', ')
        } else {
          user.roles = null
        }
      })
      totalItems.value = response.totalElements
    })
    .catch((error) => {
      console.log(error)
      if (error.response.status === 403) {
        // Handle permission error
        console.log(error)
      }
    })
    .finally(() => {
      setLoading(false)
    })
}
