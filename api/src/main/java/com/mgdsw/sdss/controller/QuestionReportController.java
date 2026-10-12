/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.controller;


import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.surveys.questions.ReportSdssSurveyQuestionResponse;
import com.mgdsw.sdss.service.QuestionReportService;
import com.mgdsw.sdss.service.SdssSurveyQuestionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

@Validated
@RestController
@RequestMapping("/api/v1/question-reports")
@Tag(name = "Sdss Questions Reports Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class QuestionReportController {

    private final QuestionReportService questionReportService;
    private final SdssSurveyQuestionService surveyQuestionService;

    @GetMapping(value = "/question-details")
    public ResponseEntity<SuccessResponse<ReportSdssSurveyQuestionResponse>> retrieveQuestionAnalysisReportDetailsById(@RequestParam Integer questionId) {
        return ControllerResponseHelper.okResponse(
                questionReportService.retrieveQuestionAnalysisReportDetailsById(surveyQuestionService.findById(questionId))
        );
    }
}
