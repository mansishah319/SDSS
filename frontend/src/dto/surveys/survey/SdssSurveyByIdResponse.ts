/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { LookupInfoResponse } from '@/dto/lookup/LookupInfoResponse.ts'
import { SdssSurveyRoundResponse } from '@/dto/surveys/round/SdssSurveyRoundResponse.ts'
import { SdssSurveyQuestionResponse } from '@/dto/surveys/question/SdssSurveyQuestionResponse.ts'

export interface SurveyInvitedUserEmailStatusResponse {
  employeeGrpNumber: number
  fullName?: string
  emailAddress?: string
  departmentName?: string
  jobName?: string
  sent?: boolean
}

export interface SdssSurveyByIdResponse extends LookupInfoResponse {
  active?: boolean
  mode?: string
  jobList?: string[]
  departmentList?: string[]
  allowedGrpList?: number[]
  invitedUsers?: SurveyInvitedUserEmailStatusResponse[]
  currentUserIsTheSurveyModerator?: boolean
  isAllowedToUpdateOrDelete?: boolean
  rounds?: SdssSurveyRoundResponse[]
  questions?: SdssSurveyQuestionResponse[]
  localStatus?: string
}
