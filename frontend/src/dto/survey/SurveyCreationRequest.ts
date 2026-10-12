/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface SurveyCreationRequest {
  challengeId: number // Challenge ID (required)
  solutionId: number // Solution ID (required)
  rating?: number // Rating (optional)
  comments?: string // Comments (optional)
  howToApply?: string // How to Apply (optional)
  applicableToApply?: boolean // Applicable to Apply (optional)
}
