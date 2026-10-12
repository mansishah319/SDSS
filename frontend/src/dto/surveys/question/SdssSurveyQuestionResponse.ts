/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'
import { SdssSurveyQuestionOptionsResponse } from '@/dto/surveys/options/SdssSurveyQuestionOptionsResponse.ts'

export interface SdssSurveyQuestionResponse extends BaseModelResponse {
  options?: SdssSurveyQuestionOptionsResponse[]
  currentUserIsTheSurveyModerator: boolean
  questionTypeKey: string
  localQuestionType: string
}
