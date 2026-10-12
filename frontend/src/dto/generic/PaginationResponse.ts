/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse'
import type { BaseResponse } from '@/dto/generic/BaseResponse'

export interface PaginationResponse<T extends BaseModelResponse> extends BaseResponse {
  totalElements: number
  numberOfPages: number
  pageNumber: number
  pageSize: number
  data: T[]
}
