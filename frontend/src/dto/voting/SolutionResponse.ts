/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface SolutionResponse extends BaseModelResponse {
  solutionText: string
  isFinal?: boolean
  isAdminAgree?: boolean
  queLevel: number
  solutionColor: number
  voteColor: number
  voteCount: number
  point: number
  rank: number
}
