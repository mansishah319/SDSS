/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.dto.surveys.questions.ReportSdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.service.SdssSurveyUserQuestionAnswersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 20 Dec 2024
 */

@Component
@RequiredArgsConstructor
public class SdssQuestionReportSurveyMapper {

    private final SdssSurveyUserQuestionAnswersService sdssSurveyUserQuestionAnswersService;
    private final SdssSurveyAnswersReportResponseMapper surveyAnswersReportResponseMapper;
    private final SdssSurveyQuestionOptionsMapper sdssSurveyQuestionOptionsMapper;

    public ReportSdssSurveyQuestionResponse toResponseReportSdssSurveyQuestionResponse(SdssSurveyQuestion entity) {
        if (entity == null) {
            return null;
        }

        List<SdssSurveyQuestionOptionsResponse> options = entity.getOptions().stream()
                .map(sdssSurveyQuestionOptionsMapper::toResponse)
                .collect(Collectors.toList());

        return ReportSdssSurveyQuestionResponse.builder()
                .usersSurveyAnswers(
                        sdssSurveyUserQuestionAnswersService.retrieveAllAnswersForAllUsersByQuestion(entity).
                                stream().map(surveyAnswersReportResponseMapper::mapToUserSurveyAnswersReportResponse).collect(Collectors.toList())
                )
                .userAnswersStatistics(sdssSurveyUserQuestionAnswersService.getStatisticsByQuestionIdAndTypeForFinalReport(entity))
                .questionAnalysis(sdssSurveyUserQuestionAnswersService.generateReportSurveyQuestionAnalysis(entity))
                .showPublicQuestions(entity.getShowPublicQuestions())
                .descriptionAr(entity.getDescriptionAr())
                .descriptionEn(entity.getDescriptionEn())
                .questionType(entity.getQuestionType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .titleEn(entity.getTitleEn())
                .titleAr(entity.getTitleAr())
                .options(options)
                .id(entity.getId())
                .build();
    }
}
