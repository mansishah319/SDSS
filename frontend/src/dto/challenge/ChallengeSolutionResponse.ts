/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse'

export interface ChallengeSolutionResponse extends BaseModelResponse {
  isFinal: boolean
  isAdminAgree: boolean
  isCurrentUserVoted: boolean
  queLevel: number
  solutionColor: number
  voteColor: number
  voteCount: number
  point: number
  rank: number
  solutionText?: string
  finalRate: number
  initPublic: boolean
}
