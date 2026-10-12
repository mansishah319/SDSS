/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface BaseEntity<I> {
  id: I
  createdBy: string
  createdAt: string // Use Date object
  updatedBy: string
  updatedAt: string // Use Date object
}

// SolutionResponse Interface
export interface SolutionResponse extends BaseEntity<number> {
  solutionText: string
  isFinal: boolean | null
  isAdminAgree: boolean | null
  queLevel: number
  solutionColor: number
  voteColor: number
  voteCount: number
  point: number
  rank: number
}

// ChallengeUsersResponse Interface
export interface ChallengeUsersResponse extends BaseEntity<number> {
  fullName: string
  base64Image: string | null
  onlineStatus: boolean | null
  email: string
}

// ChallengeSurveyResponse Interface
export interface ChallengeSurveyResponse extends BaseEntity<number> {
  status: string
  surveyStatus: string
  localTitle: string
  titleAr: string
  titleEn: string
  visibility: string
  localDescription: string
  descriptionAr: string
  descriptionEn: string
  moderatorGrpNumber: number | null
  allowImmediateAccess: boolean
  departmentId: string
  departmentName: string
  subDepartmentId: string
  subDepartmentName: string
  sectionId: string
  sectionName: string
  startTime: string // Use Date object
  endTime: string // Use Date object
}

// SurveyForSolutionResponse Interface
export interface SurveyForSolutionResponse extends BaseEntity<number> {
  rating: number
  comments: string
  applicableToApply: boolean
  howToApply: string
  solution: SolutionResponse
  user: ChallengeUsersResponse
  challenges: ChallengeSurveyResponse
}
