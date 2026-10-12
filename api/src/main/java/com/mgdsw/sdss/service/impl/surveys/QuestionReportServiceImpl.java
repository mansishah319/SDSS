/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import com.mgdsw.sdss.dto.surveys.questions.ReportSdssSurveyQuestionResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.mapper.SdssQuestionReportSurveyMapper;
import com.mgdsw.sdss.service.QuestionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionReportServiceImpl implements QuestionReportService {

    private final SdssQuestionReportSurveyMapper questionReportSurveyMapper;

    @Override
    @Transactional(readOnly = true)
    public ReportSdssSurveyQuestionResponse retrieveQuestionAnalysisReportDetailsById(SdssSurveyQuestion question) {
        return questionReportSurveyMapper.toResponseReportSdssSurveyQuestionResponse(question);
    }
}
