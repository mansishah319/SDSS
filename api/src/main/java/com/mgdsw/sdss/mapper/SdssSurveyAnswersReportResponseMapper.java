/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.common.utils.CryptoUtil;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.dto.surveys.round.UserReportSdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.round.UserSurveyAnswersReportResponse;
import com.mgdsw.sdss.dto.surveys.user.ReportInvitedSurveyUserResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestionOptions;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUserQuestionAnswers;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SdssSurveyAnswersReportResponseMapper {

    private final CryptoUtil cryptoUtil;

    public UserSurveyAnswersReportResponse mapToUserSurveyAnswersReportResponse(SdssSurveyUserQuestionAnswers answer) {
        return UserSurveyAnswersReportResponse.builder()
                .id(answer.getId())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .createdBy(answer.getCreatedBy())
                .updatedBy(answer.getUpdatedBy())
                .correct(answer.getCorrect())
                .responseText(answer.getResponseText())
                .impactSeverity(answer.getImpactSeverity())
                .probabilityOfOccurrence(answer.getProbabilityOfOccurrence())
                .user(mapToReportInvitedSurveyUserResponse(answer.getUser()))
                .selectedOptions(mapToSdssSurveyQuestionOptionsResponseList(answer.getSelectedOptions()))
                .question(mapToUserReportSdssSurveyQuestionResponse(answer.getQuestion()))
                .build();
    }

    private ReportInvitedSurveyUserResponse mapToReportInvitedSurveyUserResponse(SdssSurveyUsers user) {
        if (user == null) {
            return null;
        }
        return ReportInvitedSurveyUserResponse.builder()
                .id(user.getId())
                .employeeGrpNumber(user.getEmployeeGrpNumber())
                .employeeInfoKey(user.getEmployeeInfoKey())
                .employeeInfo(cryptoUtil.decrypt(user.getEmployeeInfoKey(), EmployeeInfoKeyResponse.class))
                .build();
    }

    private List<SdssSurveyQuestionOptionsResponse> mapToSdssSurveyQuestionOptionsResponseList(List<SdssSurveyQuestionOptions> options) {
        if (options == null) {
            return Collections.emptyList();
        }
        return options.stream()
                .map(this::mapToSdssSurveyQuestionOptionsResponse)
                .collect(Collectors.toList());
    }

    private SdssSurveyQuestionOptionsResponse mapToSdssSurveyQuestionOptionsResponse(SdssSurveyQuestionOptions option) {
        if (option == null) {
            return null;
        }
        return SdssSurveyQuestionOptionsResponse.builder()
                .id(option.getId())
                .createdAt(option.getCreatedAt())
                .updatedAt(option.getUpdatedAt())
                .createdBy(option.getCreatedBy())
                .updatedBy(option.getUpdatedBy())
                .optionEn(option.getOptionEn())
                .optionAr(option.getOptionAr())
                .build();
    }

    private UserReportSdssSurveyQuestionResponse mapToUserReportSdssSurveyQuestionResponse(SdssSurveyQuestion question) {
        if (question == null) {
            return null;
        }
        return UserReportSdssSurveyQuestionResponse.builder()
                .id(question.getId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .createdBy(question.getCreatedBy())
                .updatedBy(question.getUpdatedBy())
                .titleEn(question.getTitleEn())
                .titleAr(question.getTitleAr())
                .descriptionEn(question.getDescriptionEn())
                .descriptionAr(question.getDescriptionAr())
                .questionType(question.getQuestionType())
                .showPublicQuestions(question.getShowPublicQuestions())
                .build();
    }
}
