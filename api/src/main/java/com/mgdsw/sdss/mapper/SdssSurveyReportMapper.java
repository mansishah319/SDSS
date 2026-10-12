/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.dto.surveys.ReportSdssSurveyResponse;
import com.mgdsw.sdss.dto.surveys.questions.ReportSdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.round.ReportSdssSurveyRoundResponse;
import com.mgdsw.sdss.dto.surveys.round.UserSurveyAnswersReportResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUserQuestionAnswers;
import com.mgdsw.sdss.service.SdssSurveyInvitationService;
import com.mgdsw.sdss.service.SdssSurveyQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
public class SdssSurveyReportMapper {

    private final SdssSurveyQuestionService sdssSurveyQuestionService;
    private final SdssQuestionReportSurveyMapper questionReportMapper;
    private final SdssSurveyInvitationService sdssSurveyInvitationService;
    private final SdssSurveyAnswersReportResponseMapper surveyAnswersReportResponseMapper;

    public ReportSdssSurveyRoundResponse toReportSdssSurveyRoundResponse(SdssSurveyRound entity) {
        if (entity == null) {
            return null;
        }

        List<ReportSdssSurveyQuestionResponse> questions = this.retrieveQuestionsReportsByRound(entity);
        SdssSurvey survey = entity.getSurvey();

        return ReportSdssSurveyRoundResponse.builder()
                .totalNumberOfInvitedUsers(sdssSurveyInvitationService.countDistinctInvitedUsersBySurveyAndSurveyRound(survey, entity))
                .descriptionAr(entity.getDescriptionAr())
                .descriptionEn(entity.getDescriptionEn())
                .manualHandle(entity.getManualHandle())
                .roundNumber(entity.getRoundNumber())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .startTime(entity.getStartTime())
                .titleAr(entity.getTitleAr())
                .titleEn(entity.getTitleEn())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .active(entity.getActive())
                .questions(questions)
                .id(entity.getId())
                .survey(
                        ReportSdssSurveyResponse.builder()
                                .descriptionAr(survey.getDescriptionAr())
                                .descriptionEn(survey.getDescriptionEn())
                                .allowedGrpList(survey.getAllowedGrpList())
                                .departmentList(survey.getDepartmentList())
                                .numberOfRounds(survey.getNumberOfRounds())
                                .manualHandle(survey.getManualHandle())
                                .createdAt(survey.getCreatedAt())
                                .updatedAt(survey.getUpdatedAt())
                                .createdBy(survey.getCreatedBy())
                                .updatedBy(survey.getUpdatedBy())
                                .titleAr(survey.getTitleAr())
                                .titleEn(survey.getTitleEn())
                                .jobList(survey.getJobList())
                                .active(survey.getActive())
                                .status(survey.getStatus())
                                .id(survey.getId())
                                .build()
                )
                .build();
    }

    public UserSurveyAnswersReportResponse mapToUserSurveyAnswersReportResponse(SdssSurveyUserQuestionAnswers answer) {
        return surveyAnswersReportResponseMapper.mapToUserSurveyAnswersReportResponse(answer);
    }

    private List<ReportSdssSurveyQuestionResponse> retrieveQuestionsReportsByRound(SdssSurveyRound surveyRound) {
        return sdssSurveyQuestionService.findByRound(surveyRound)
                .stream()
                .map(questionReportMapper::toResponseReportSdssSurveyQuestionResponse)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> list.isEmpty() ? Collections.emptyList() : list));
    }
}
