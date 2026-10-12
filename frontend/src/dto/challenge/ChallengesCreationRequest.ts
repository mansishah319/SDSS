/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface ChallengesCreationRequest {
  invitedUsersIds: number[]
  validatorsIds: number[]
  moderatorId: number | null
  assistantModeratorGrp: number | null
  departmentId: string
  departmentName: string
  subDepartmentId: string
  subDepartmentName: string
  sectionId: string
  sectionName: string
  startTime: string
  endTime: string
  visibility: string
  hashtagTexts: string[]
  allowImmediateAccess: boolean
  strategicGoalsId: number | null
  strategicDirectionId: number | null
  futureStrategicGoalsId: number | null
  futureStrategicDirectionId: number | null
  youtubeLinks: string[]
  titleAr: string
  titleEn: string
  descriptionAr: string
  descriptionEn: string
  challengeRemindersDateTimeList: string[]
  solutionsTexts: string[]
  documentsIds: number[]
  topicClassification: number | null
  levelOfDecision: number | null
  importanceOfDecision: number | null
  impact: number | null
  field: number | null
  urgent: boolean
  committeeId: number | null
}
