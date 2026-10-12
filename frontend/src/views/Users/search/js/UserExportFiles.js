/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import UserClientService from '@/services/user/UserClientService'
import { setupDataTableRequest } from '@/utils/dataTableRequestUtils'

export default async function exportExcelFile(dataTableOptions, dataTableRequest, tableResponse, setLoading) {
  setLoading(true)
  setupDataTableRequest(dataTableOptions, dataTableRequest)

  try {
    await UserClientService.downloadExcelFile(
      dataTableRequest.data,
      dataTableRequest.currentPage,
      dataTableRequest.pageSize,
      dataTableRequest.sortBy,
      dataTableRequest.sortDesc ? 'desc' : 'asc'
    )
    console.log('Excel file downloaded successfully!')
  } catch (error) {
    console.error('Error exporting to Excel:', error)
  } finally {
    setLoading(false)
  }
}
