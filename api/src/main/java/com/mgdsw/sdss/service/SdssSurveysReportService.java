/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.dto.surveys.round.ReportSdssSurveyRoundResponse;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 20 Dec 2024
 */
public interface SdssSurveysReportService {

    ReportSdssSurveyRoundResponse retrieveAllReportsForCurrentSurveyModerator(Integer roundId);

    byte[] generateSurveyByRoundReportPDF(Integer roundId);
}
