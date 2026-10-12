/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface ChallengeAdminDashboardResponse extends BaseModelResponse {
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
  sectorName: string
  departmentId: string
  departmentName: string
  localChallengeStatus: string
  status: string
  localTitle: string
  localDescription: string
  statusLocal: string
}
