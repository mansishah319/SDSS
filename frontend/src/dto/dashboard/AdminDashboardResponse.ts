/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { TheUserDashboardResponse } from '@/dto/userDashboard/TheUserDashboardResponse'

export interface AdminDashboardResponse {
  isCommander: boolean
  isModeratorOrAdminOrCommander: boolean
  userDashboardResponse: TheUserDashboardResponse
  challengeDashboardResponse: ChallengeDashboardResponse
  usersDashboardResponse: UsersDashboardResponse
  departmentChallengeStatsResponseList: DepartmentChallengeStatsResponse[]
  futureStrategicGoalsResponseList: FutureStrategicGoalDashboardResponse[]
  futureStrategicDirectionsResponseList: FutureStrategicGoalDashboardResponse[]
}

export interface ChallengeDashboardResponse {
  totalChallenges: number
  completedCount: number
  openCount: number
  closedCount: number
  votingCount: number
  revisedCount: number
  pausedCount: number
  underReviewCount: number
  newCount: number
  urgentCount: number
  recentlyOpenedPercentage: number
  completedChallengesList: Challenge[]
  openChallengesList: Challenge[]
  closedChallengesList: Challenge[]
  votingChallengesList: Challenge[]
  revisedChallengesList: Challenge[]
  pausedChallengesList: Challenge[]
  underReviewChallengesList: Challenge[]
  newChallengesList: Challenge[]
  urgentChallengesList: Challenge[]
  challengesResponseList: Challenge[]
}

export interface Challenge {
  id: number
  createdAt: string
  updatedAt: string
  titleAr: string
  titleEn: string
  descriptionAr: string
  descriptionEn: string
  topicClassification: string
  levelOfDecision: string
  importanceOfDecision: string
  impact: string
  field: string
  sectorId: number
  localDescription: string
  localTitle: string
}

export interface User {
  id: number
  createdBy: string
  createdAt: string
  updatedAt: string
  firstname: string
  lastname: string
  grpNumber: string
  language: string
  middleName?: string
  status: string
  mobile: string
  email: string
  alternativeEmail?: string
  type: string
  roles?: Role[]
  fullName: string
  statusLocal: string
}

export interface Role {
  id: number
  createdBy: string
  createdAt: string
  updatedBy: string
  updatedAt: string
  nameAr: string
  nameEn: string
}

export interface UsersDashboardResponse {
  activeUsersCount: number
  deletedUsersCount: number
  notActiveUsersCount: number
  internalUsersCount: number
  externalUsersCount: number
  totalUsersCount: number
  activeUsersList: User[]
  externalUsersList: User[]
  internalAdminUsersList: User[]
  externalGuestUsersList: User[]
  deletedUsersList: User[]
  notActiveUsersList: User[]
}

export interface DepartmentChallengeStatsResponse {
  shortName: string
  departmentId: number
  challengeCount: number
  departmentName: string
}

export interface FutureStrategicGoalDashboardResponse {
  id: number
  createdBy: string
  createdAt: string
  updatedBy: string
  updatedAt: string
  nameAr: string
  nameEn: string
  countOfChallenges: number
  challengesResponseList: ChallengeStrategicGoalDashboardResponse[]
  localName: string
}

export interface ChallengeStrategicGoalDashboardResponse {
  id: number
  createdAt?: string
  createdBy?: string
  updatedBy: string
  updatedAt: string
  status: string
  surveyStatus: string
  titleAr: string
  titleEn: string
  visibility: string
  descriptionAr: string
  descriptionEn: string
  moderatorGrpNumber: number
  allowImmediateAccess: boolean
  departmentId: string
  departmentName?: string
  subDepartmentId?: string
  subDepartmentName?: string
  sectionId?: string
  sectionName?: string
  startTime: string
  endTime: string
  isFinal: number
  queLevel: number
}
