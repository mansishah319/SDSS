/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse'
import { UserInvitationCountResponse } from '@/dto/challenge/UserInvitationCountResponse.ts'
import { UserDropDownListResponse } from '@/dto/challenge/UserDropDownListResponse.ts'
import { DocumentsChallengeResponse } from '@/dto/challenge/DocumentsChallengeResponse.ts'
import { ChallengeUsersResponse } from '@/dto/challenge/ChallengeUsersResponse.ts'
import { ChatHistory } from '@/dto/challenge/ChatHistory.ts'
import { SolutionFullResponse } from '@/dto/challenge/SolutionFullResponse.ts'
import { ChallengeSettingsResponse } from '@/dto/challenge/ChallengeSettingsResponse.ts'
import { DropdownResponse } from '@/dto/lookup/general/DropdownResponse.ts'
import { ChallengeReminders } from '@/dto/reminder/ChallengeReminders.ts'

export interface ChallengesResponse extends BaseModelResponse {
  status: string
  localTitle: string
  titleAr: string
  titleEn: string
  visibility: string
  localDescription: string
  descriptionAr: string
  descriptionEn: string
  moderator: UserDropDownListResponse
  assistantModerator: UserDropDownListResponse
  moderatorGrpNumber: number

  sectorId: number
  sectorName: string

  departmentId: string
  departmentName: string

  committeeId: number
  solutionsTexts: string[]
  hashtagTexts: string[]

  subDepartmentId: string
  subDepartmentName: string

  sectionId: string
  sectionName: string

  startTime: string
  endTime: string

  field: string
  impact: string
  levelOfDecision: string
  topicClassification: string
  importanceOfDecision: string

  fieldResponse: ChallengeSettingsResponse
  impactResponse: ChallengeSettingsResponse
  levelOfDecisionResponse: ChallengeSettingsResponse
  topicClassificationResponse: ChallengeSettingsResponse
  importanceOfDecisionResponse: ChallengeSettingsResponse

  allowImmediateAccess: boolean
  isAllowedToUpdateOrDelete: boolean

  youtubeUrls: string[]

  votingTime: string

  isFinal: number
  queLevel: number

  votingResultResponse: VotingResultResponse
  userInvitationCountedResponse: UserInvitationCountResponse
  validatorList: ChallengeUsersResponse[]
  teamMembersList: ChallengeUsersResponse[]
  challengeDocsResponseList: DocumentsChallengeResponse[]
  chatHistoryList: ChatHistory[]
  solutionFullResponseList: SolutionFullResponse[]
  solutionsPerStageResponseList: SolutionsPerStageResponse[]
  strategicDirection: DropdownResponse
  strategicGoals: DropdownResponse
  futureStrategicDirection: DropdownResponse
  futureStrategicGoals: DropdownResponse
  challengeReminders: ChallengeReminders[]

  currentUserIsTheChallengeModeratorOrAssistantModerator: boolean
}

export interface VotingResultResponse {
  solutionTextList?: string[]
  countOfVotes?: number[]
  solutionVotingResult?: { [key: string]: number }
  totalVotes: number
  percentageOfVotes: number[]
}

export interface VotingResultPerStageResponse {
  stageNumber?: number
  votingResultResponse?: VotingResultResponse
}

export interface SolutionsPerStageResponse {
  stageNumber?: number
  solutionResponseList?: SolutionFullResponse[]
  votingResultPerStageResponse: VotingResultPerStageResponse
}
