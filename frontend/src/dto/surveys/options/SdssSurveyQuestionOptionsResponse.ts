/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { BaseModelResponse } from '@/dto/generic/BaseModelResponse'

export interface SdssSurveyQuestionOptionsResponse extends BaseModelResponse {
  optionEn?: string
  optionAr?: string
  localOption: string
  currentUserIsTheSurveyModerator: boolean
}
