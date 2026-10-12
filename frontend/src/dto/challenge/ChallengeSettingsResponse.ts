/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface ChallengeSettingsResponse extends BaseModelResponse {
  type: string
  nameEn: string
  nameAr: string
  localName: string
  name: string
}
