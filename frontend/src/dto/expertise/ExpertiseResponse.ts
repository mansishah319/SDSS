/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface ExpertiseResponse extends BaseModelResponse {
  nameAr: string
  nameEn: string
  localName: string
}
