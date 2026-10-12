/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface SdssSurveyRoundRequest {
  titleEn?: string | null // Default: ROUND_DEFAULT_TITLE_EN
  titleAr?: string | null // Default: ROUND_DEFAULT_TITLE_AR
  descriptionEn?: string | null // Description in English, optional
  descriptionAr?: string | null // Description in Arabic, optional
  manualHandle?: boolean | null // Whether manual handling is required
  startTime?: string | null // Start time of the round, optional
  endTime?: string | null // End time of the round, optional
  roundNumber?: number | null // The round number, required
}
