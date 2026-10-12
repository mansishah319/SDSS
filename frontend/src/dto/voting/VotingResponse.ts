/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'
import { SolutionResponse } from '@/dto/voting/SolutionResponse.ts'

export interface VotingResponse extends BaseModelResponse {
  queLevel: number
  userSorting: string
  solution: SolutionResponse
}
