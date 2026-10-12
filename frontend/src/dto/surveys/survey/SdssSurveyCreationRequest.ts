/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { LookupInfoRequest } from '@/dto/lookup/LookupInfoRequest.ts'
import { SdssSurveyRoundRequest } from '@/dto/surveys/round/SdssSurveyRoundRequest.ts'

export interface SdssSurveyCreationRequest extends LookupInfoRequest {
  manualHandle?: boolean
  allowedGrpList?: number[]
  numberOfRounds?: number
  roundRequestList?: SdssSurveyRoundRequest[]
}
