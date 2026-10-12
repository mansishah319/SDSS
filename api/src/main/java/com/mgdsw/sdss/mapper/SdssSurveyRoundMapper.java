/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.common.utils.DateTimeHandlerUtil;
import com.mgdsw.sdss.dto.surveys.ReportSdssSurveyResponse;
import com.mgdsw.sdss.dto.surveys.enduser.PublicQuestionResponse;
import com.mgdsw.sdss.dto.surveys.enduser.PublicRoundResponse;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.round.RoundReportMainResponse;
import com.mgdsw.sdss.dto.surveys.round.SdssSurveyRoundRequest;
import com.mgdsw.sdss.dto.surveys.round.SdssSurveyRoundResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.enums.SdssStatus;
import com.mgdsw.sdss.service.SdssSurveyQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 13 Nov 2024
 */

@Component
@RequiredArgsConstructor
public class SdssSurveyRoundMapper {

    private final SdssSurveyQuestionService sdssSurveyQuestionService;
    private final SdssSurveyQuestionMapper sdssSurveyQuestionMapper;

    public SdssSurveyRound mapToEntity(SdssSurveyRound surveyRound, SdssSurveyRoundRequest request, SdssSurvey survey) {
        Boolean isManualHandle = request.getManualHandle();
        if (Objects.nonNull(isManualHandle)) {
            surveyRound.setManualHandle(isManualHandle);
            if (Boolean.FALSE.equals(isManualHandle)) {
                this.handleAutomaticProcess(surveyRound, request);
            }
        }
        surveyRound.setStatus(SdssStatus.PENDING);
        surveyRound.setActive(Boolean.FALSE);
        surveyRound.setSurvey(survey);
        return surveyRound;
    }

    public SdssSurveyRoundResponse toResponse(SdssSurveyRound entity) {
        if (entity == null) {
            return null;
        }
        List<SdssSurveyQuestionResponse> questions = this.retrieveQuestionsByRound(entity);

        return SdssSurveyRoundResponse.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .titleAr(entity.getTitleAr())
                .titleEn(entity.getTitleEn())
                .descriptionAr(entity.getDescriptionAr())
                .descriptionEn(entity.getDescriptionEn())
                .manualHandle(entity.getManualHandle())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .roundNumber(entity.getRoundNumber())
                .status(entity.getStatus())
                .active(entity.getActive())
                .questions(questions)
                .build();
    }

    public PublicRoundResponse toPublicRoundResponse(SdssSurveyRound entity) {
        if (entity == null) {
            return null;
        }

        List<PublicQuestionResponse> questions =
                sdssSurveyQuestionService.findByRound(entity)
                        .stream()
                        .map(sdssSurveyQuestionMapper::toPublicQuestionResponse)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), list -> list.isEmpty() ? Collections.emptyList() : list));


        return PublicRoundResponse.builder()
                .descriptionEn(entity.getDescriptionEn())
                .descriptionAr(entity.getDescriptionAr())
                .manualHandle(entity.getManualHandle())
                .roundNumber(entity.getRoundNumber())
                .updatedAt(entity.getUpdatedAt())
                .startTime(entity.getStartTime())
                .titleAr(entity.getTitleAr())
                .titleEn(entity.getTitleEn())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .active(entity.getActive())
                .questions(questions)
                .id(entity.getId())
                .build();
    }

    public RoundReportMainResponse toRoundReportMainResponse(SdssSurveyRound entity) {
        if (entity == null) {
            return null;
        }

        SdssSurvey survey = entity.getSurvey();

        return RoundReportMainResponse.builder()
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
                .id(entity.getId())
                .survey(
                        ReportSdssSurveyResponse.builder()
                                .allowedGrpList(survey.getAllowedGrpList())
                                .departmentList(survey.getDepartmentList())
                                .numberOfRounds(survey.getNumberOfRounds())
                                .descriptionAr(survey.getDescriptionAr())
                                .descriptionEn(survey.getDescriptionEn())
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

    private void handleAutomaticProcess(SdssSurveyRound surveyRound, SdssSurveyRoundRequest request) {
        surveyRound.setStartTime(DateTimeHandlerUtil.parseToLocalDateTime(request.getStartTime()));
        surveyRound.setEndTime(DateTimeHandlerUtil.parseToLocalDateTime(request.getEndTime()));
    }

    private List<SdssSurveyQuestionResponse> retrieveQuestionsByRound(SdssSurveyRound surveyRound) {
        return sdssSurveyQuestionService.findByRound(surveyRound)
                .stream()
                .map(sdssSurveyQuestionMapper::toResponse)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> list.isEmpty() ? Collections.emptyList() : list));
    }
}
