/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.dto.surveys.enduser.PublicOptionsResponse;
import com.mgdsw.sdss.dto.surveys.user.InvitedUserSingleAnswerRequest;
import com.mgdsw.sdss.dto.surveys.user.SdssSurveyUserQuestionAnswersResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestionOptions;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUserQuestionAnswers;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import com.mgdsw.sdss.enums.QuestionType;
import com.mgdsw.sdss.service.SdssSurveyQuestionOptionsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 28 Nov 2024
 */

@Component
@RequiredArgsConstructor
public class SurveyUserQuestionAnswersMapper {

    private final SdssSurveyQuestionOptionsMapper sdssSurveyQuestionOptionsMapper;
    private final SdssSurveyQuestionMapper sdssSurveyQuestionMapper;
    private final InvitedSurveyUserMapper invitedSurveyUserMapper;

    private final SdssSurveyQuestionOptionsService surveyQuestionOptionsService;

    public SdssSurveyUserQuestionAnswersResponse toSdssSurveyUserQuestionAnswersResponse(SdssSurveyUserQuestionAnswers entity) {

        List<PublicOptionsResponse> selectedOptions = Optional.ofNullable(entity.getSelectedOptions())
                .map(options -> options.stream()
                        .map(sdssSurveyQuestionOptionsMapper::toPublicOptionsResponse)
                        .collect(Collectors.toList()))
                .orElse(null);

        return SdssSurveyUserQuestionAnswersResponse.builder()
                .id(entity.getId())
                .correct(entity.getCorrect())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .selectedOptions(selectedOptions)
                .responseText(entity.getResponseText())
                .impactSeverity(entity.getImpactSeverity())
                .probabilityOfOccurrence(entity.getProbabilityOfOccurrence())
                .user(invitedSurveyUserMapper.toPublicEndUserResponse(entity.getUser()))
                .question(sdssSurveyQuestionMapper.toPublicQuestionResponse(entity.getQuestion()))
                .build();
    }

    public SdssSurveyUserQuestionAnswers toEntity(InvitedUserSingleAnswerRequest request, SdssSurveyUsers invitedUser, SdssSurveyQuestion surveyQuestion) {
        // Check based on the Question Type Key Which answers must be null
        InvitedUserSingleAnswerRequest validatedRequest = checkBasedOnQuestionTypeFieldsMustBeNull(request, surveyQuestion.getQuestionType());

        Boolean userAnswerForTrueFalseQuestions = validatedRequest.getCorrect();
        List<Integer> requestOptionIds = validatedRequest.getOptionIds();
        String answerText = validatedRequest.getResponseText();
        Double impactSeverity = request.getImpactSeverity();
        Double probabilityOfOccurrence = request.getProbabilityOfOccurrence();

        return SdssSurveyUserQuestionAnswers.builder()
                .user(invitedUser)
                .question(surveyQuestion)
                .impactSeverity(Objects.nonNull(impactSeverity) ? impactSeverity : null)
                .probabilityOfOccurrence(Objects.nonNull(probabilityOfOccurrence) ? probabilityOfOccurrence : null)
                .correct(Objects.nonNull(userAnswerForTrueFalseQuestions) ? userAnswerForTrueFalseQuestions : null)
                .responseText(Objects.nonNull(answerText) && StringUtils.isNotBlank(answerText) ? answerText : null)
                .selectedOptions(Objects.nonNull(requestOptionIds) && ObjectUtils.isNotEmpty(requestOptionIds) ? requestOptionIds.stream().map(surveyQuestionOptionsService::findById).collect(Collectors.toList()) : null)
                .build();
    }

    public InvitedUserSingleAnswerRequest mapToRequest(SdssSurveyUserQuestionAnswers entity) {
        List<Integer> selectedOptionIds = Optional.ofNullable(entity.getSelectedOptions())
                .orElse(Collections.emptyList())
                .stream()
                .map(SdssSurveyQuestionOptions::getId)
                .collect(Collectors.toList());

        return InvitedUserSingleAnswerRequest.builder()
                .correct(entity.getCorrect())
                .responseText(entity.getResponseText())
                .questionId(entity.getQuestion().getId())
                .impactSeverity(entity.getImpactSeverity())
                .probabilityOfOccurrence(entity.getProbabilityOfOccurrence())
                .optionIds(selectedOptionIds.isEmpty() ? Collections.emptyList() : selectedOptionIds)
                .build();
    }

    public SdssSurveyUserQuestionAnswers updateEntity(SdssSurveyUserQuestionAnswers existingEntity, InvitedUserSingleAnswerRequest request, SdssSurveyQuestion surveyQuestion) {
        InvitedUserSingleAnswerRequest validatedRequest = checkBasedOnQuestionTypeFieldsMustBeNull(request, surveyQuestion.getQuestionType());
        if (Objects.nonNull(validatedRequest.getCorrect())) {
            existingEntity.setCorrect(validatedRequest.getCorrect());
        }
        if (Objects.nonNull(validatedRequest.getResponseText())) {
            existingEntity.setResponseText(validatedRequest.getResponseText());
        }
        if (Objects.nonNull(validatedRequest.getImpactSeverity())) {
            existingEntity.setImpactSeverity(validatedRequest.getImpactSeverity());
        }
        if (Objects.nonNull(validatedRequest.getProbabilityOfOccurrence())) {
            existingEntity.setProbabilityOfOccurrence(validatedRequest.getProbabilityOfOccurrence());
        }
        if (Objects.nonNull(validatedRequest.getOptionIds())) {
            existingEntity.setSelectedOptions(validatedRequest.getOptionIds().stream().map(surveyQuestionOptionsService::findById).collect(Collectors.toList()));
        }
        return existingEntity;
    }

    private InvitedUserSingleAnswerRequest checkBasedOnQuestionTypeFieldsMustBeNull(InvitedUserSingleAnswerRequest request, QuestionType questionType) {
        if (Objects.isNull(questionType)) {
            return request;
        }

        switch (questionType) {
            case TEXT:
                request.setOptionIds(null);
                request.setCorrect(null);
                break;
            case TRUE_FALSE:
                request.setOptionIds(null);
                request.setResponseText(null);
                break;
            case SINGLE_SELECT:
                this.checkThatAnswerHasSingleElement(request.getOptionIds());
                request.setCorrect(null);
                request.setResponseText(null);
                break;
            case MULTI_SELECT:
                request.setCorrect(null);
                request.setResponseText(null);
                break;
            default:
                return request;
        }
        return request;
    }

    private void checkThatAnswerHasSingleElement(List<Integer> optionIds) {
        if (optionIds.size() > 1) {
            throw new CustomDataException("OptionIds must contain at most one element.", "3325");
        }
    }
}
