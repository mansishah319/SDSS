/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { LookupInfoResponse } from '@/dto/lookup/LookupInfoResponse.ts'
import { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface SdssSurveyRoundResponse extends LookupInfoResponse {
  manualHandle?: boolean
  endTime?: string
  startTime?: string
  roundNumber?: number
  currentUserIsTheSurveyModerator?: boolean
  isAllowedToUpdateOrDelete?: boolean
  localStatus?: string
  status?: string
  active?: boolean
  questions?: QuestionResponse[]
}

export interface QuestionResponse extends LookupInfoResponse {
  id: number
  createdBy?: string
  createdAt?: string
  updatedBy?: string
  updatedAt?: string
  localTitle?: string
  localDescription?: string
  showPublicQuestions: boolean
  currentUserIsTheSurveyModerator?: boolean
  localQuestionType?: string
  questionTypeKey?: string
  options?: OptionResponse[]
}

export interface OptionResponse extends BaseModelResponse {
  optionEn?: string
  optionAr?: string
  localOption?: string
  currentUserIsTheSurveyModerator?: boolean
}

interface OptionsRequest {
  optionEn?: string
  optionAr?: string
}

export interface QuestionRequest {
  titleEn?: string
  titleAr?: string
  descriptionEn?: string
  descriptionAr?: string
  questionType?: string
  surveyId?: number
  roundId?: number
  optionsRequestList?: OptionsRequest[]
}
