/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { ChallengeAdminDashboardResponse } from '@/dto/challenge/ChallengeAdminDashboardResponse.ts'

export interface TheValidatorDashboardResponse {
  totalInvitationsCount: number
  totalAcceptedCount: number
  totalPendingCount: number
  totalRejectedCount: number

  votingParticipatedCount: number
  votingNotParticipatedCount: number

  pendingInvitationList: ChallengeForTheUserDashboard[]
  acceptedInvitationList: ChallengeForTheUserDashboard[]
  rejectedInvitationsList: ChallengeForTheUserDashboard[]
  completedInvitationsList: ChallengeAdminDashboardResponse[]

  requestsUnderReview: ChallengeAdminDashboardResponse[]
  reviewedChallengesList: ChallengeAdminDashboardResponse[]
  closedChallengesList: ChallengeAdminDashboardResponse[]
}

export interface ChallengeForTheUserDashboard {
  id: number
  createdAt: string
  updatedAt: string
  updatedBy: string
  createdBy: string

  titleAr: string
  titleEn: string
  startTime: string
  endTime: string
  finalDecisions: SolutionForTheUserDashboard[]
  localTitle: string
}

export interface SolutionForTheUserDashboard {
  id: number
  createdAt: string
  updatedAt: string
  updatedBy: string
  createdBy: string

  solutionText: string
  isFinal: boolean
  isAdminAgree: boolean
  queLevel: number
  solutionColor: number
  voteColor: number
  voteCount: number
  point: number
  rank: number
}
