/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import type { BaseResponse } from '@/dto/generic/BaseResponse'

export interface SuccessResponse<T> extends BaseResponse {
  data: T
}
