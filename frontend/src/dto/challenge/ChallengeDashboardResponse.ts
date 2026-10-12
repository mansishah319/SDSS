/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { ChallengeSolutionResponse } from '@/dto/challenge/ChallengeSolutionResponse.ts'
import { ChallengeUsersResponse } from '@/dto/challenge/ChallengeUsersResponse.ts'

export interface ChallengeDashboardResponse {
  id: number
  isFinal: number
  startTime: string
  endTime: string
  challengeTitle: string
  challengeDetails: string
  youtubeUrls: string[]
  challengeSolutions: ChallengeSolutionResponse[]
  status: string
  statusLocal: string
  queLevel: number
  moderator: ChallengeUsersResponse
  assistantModerator: ChallengeUsersResponse
}
