/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface LookupInfoResponse extends BaseModelResponse {
  titleEn?: string
  titleAr?: string
  descriptionEn?: string
  descriptionAr?: string
  localTitle?: string
  localDescription?: string
}
