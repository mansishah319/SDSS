/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { TheValidatorDashboardResponse } from '@/dto/userDashboard/TheValidatorDashboardResponse.ts'

export interface MyMainDashboardResponse {
  userDashboardResponse: TheUserDashboardResponse
  validatorsDashboardResponse: TheValidatorDashboardResponse
}

export interface TheUserDashboardResponse {
  totalInvitationsCount: number
  totalAcceptedCount: number
  totalPendingCount: number
  totalRejectedCount: number

  votingParticipatedCount: number
  votingNotParticipatedCount: number

  pendingInvitationList: ChallengeForTheUserDashboard[]
  acceptedInvitationList: ChallengeForTheUserDashboard[]
  rejectedInvitationsList: ChallengeForTheUserDashboard[]
  completedInvitationsList: ChallengeForTheUserDashboard[]
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
  userInvitations: UserInvitations
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

export interface UserInvitations {
  id: number
  createdBy: string
  createdAt: string
  updatedBy: string
  updatedAt: string
  user: User
}

interface User {
  id: number
  createdBy: string
  createdAt: string
  updatedBy: string
  updatedAt: string
  fullName: string
  grpNumber: string
  language: string
  status: string
  mobile: string
  email: string
  alternativeEmail: string
  type: string
  roles: Role[]
  isMailSend: boolean
  internalDocuments: boolean
  chatContents: boolean
  socialMediaFeeds: boolean
  decision: boolean
  questionView: boolean
  scrollingText: boolean
}

interface Role {
  id: number
  createdBy?: string
  createdAt?: string
  updatedBy?: string
  updatedAt?: string
  nameAr: string
  nameEn: string
}
