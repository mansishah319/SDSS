/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface ChallengeUsersResponse extends BaseModelResponse {
  grpNumber: number
  onlineStatus: string
  email: string
  base64Image: string
  fullName: string
}
