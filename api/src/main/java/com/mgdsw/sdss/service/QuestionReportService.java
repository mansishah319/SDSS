/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.dto.surveys.questions.ReportSdssSurveyQuestionResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;

public interface QuestionReportService {

    ReportSdssSurveyQuestionResponse retrieveQuestionAnalysisReportDetailsById(SdssSurveyQuestion question);
}
