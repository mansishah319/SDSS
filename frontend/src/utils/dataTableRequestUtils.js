/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// dataTableRequestUtils.js

export function setupDataTableRequest(dataTableOptions, dataTableRequest) {
  dataTableRequest.currentPage = dataTableOptions.page - 1
  dataTableRequest.pageSize = dataTableOptions.itemsPerPage
  console.log('pageSize field is -> ' + dataTableRequest.pageSize)
  console.log('currentPage field is -> ' + dataTableRequest.currentPage)

  if (dataTableOptions.sortBy && dataTableOptions.sortBy.length > 0) {
    dataTableRequest.sortBy = dataTableOptions.sortBy[0]
  } else {
    dataTableRequest.sortBy = ''
  }

  if (dataTableOptions.sortDesc && dataTableOptions.sortDesc.length > 0) {
    dataTableRequest.sortDesc = dataTableOptions.sortDesc[0]
  } else {
    dataTableRequest.sortDesc = false
  }

  if (dataTableRequest.sortBy === 'fullName') {
    dataTableRequest.sortBy = 'firstname'
  }

  if (dataTableRequest.sortBy === '') {
    dataTableRequest.sortBy = 'updatedAt'
  }

  if (dataTableRequest.data === undefined) {
    dataTableRequest.data = {}
  }

  console.log('Sorting field is -> ' + dataTableRequest.sortBy)
}
