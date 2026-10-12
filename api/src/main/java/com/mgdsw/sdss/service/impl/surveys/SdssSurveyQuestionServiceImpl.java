/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionRequest;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.questions.UpdateSurveyQuestionRequest;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.QuestionType;
import com.mgdsw.sdss.mapper.SdssSurveyQuestionMapper;
import com.mgdsw.sdss.repository.surveys.SdssSurveyQuestionRepository;
import com.mgdsw.sdss.service.SdssSurveyQuestionOptionsService;
import com.mgdsw.sdss.service.SdssSurveyQuestionService;
import com.mgdsw.sdss.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Service
@RequiredArgsConstructor
public class SdssSurveyQuestionServiceImpl extends AbstractBaseServiceImpl<Integer, SdssSurveyQuestion> implements SdssSurveyQuestionService {

    private final SdssSurveyQuestionRepository repository;

    private final SdssSurveyQuestionOptionsService optionsService;
    private final SdssSurveyQuestionMapper mapper;
    private final UserService userService;

    @Override
    protected SdssSurveyQuestionRepository repository() {
        return repository;
    }

    @Override
    @Transactional
    public SdssSurveyQuestionResponse createNewSurveyQuestion(SdssSurveyQuestionRequest questionRequest, SdssSurvey survey, SdssSurveyRound surveyRound) {
        // Firstly, check the current user is the moderator for this survey or not ---> If not throw exception
        User currentUser = userService.retrieveCurrentUser();
        this.beSureTheModeratorOfSurveyIsTheCurrentUser(currentUser.getGrpNumber(), survey.getCreatedBy());
        SdssSurveyQuestion surveyQuestion = repository.save(mapper.mapToEntity(questionRequest, survey, surveyRound));
        this.checkIfTheQuestionTypeQuestNotAcceptOptions(surveyQuestion, questionRequest);
        optionsService.createNewListOfOptionsForSpecificQuestion(surveyQuestion, questionRequest.getOptionsRequestList());
        return super.modelMapper.map(surveyQuestion, SdssSurveyQuestionResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurveyQuestion> findByRound(SdssSurveyRound round) {
        return repository.findByRound(round);
    }

    @Override
    @Transactional
    public SdssSurveyQuestionResponse updateSurveyQuestion(UpdateSurveyQuestionRequest updateRequest, Integer questionId) {
        SdssSurveyQuestion existSurveyQuestion = super.findById(questionId);
        if (Boolean.FALSE.equals(super.checkWhoCreatedThisRecordByEntity(existSurveyQuestion)))
            handleExceptionThatTheCurrentUserNotAuthorizedToMakeAnyActionForThisQuestion();
        SdssSurveyQuestion updatedSurveyQuestion = repository.save(mapper.updateEntity(updateRequest, existSurveyQuestion));
        return super.modelMapper.map(updatedSurveyQuestion, SdssSurveyQuestionResponse.class);
    }

    private void beSureTheModeratorOfSurveyIsTheCurrentUser(Long currentUserGrpNumber, String grpNumberCreatedBy) {
        if (!currentUserGrpNumber.equals(Long.valueOf(grpNumberCreatedBy))) {
            handleExceptionThatTheCurrentUserNotAuthorizedToMakeAnyActionForThisQuestion();
        }
    }

    private void checkIfTheQuestionTypeQuestNotAcceptOptions(SdssSurveyQuestion surveyQuestion, SdssSurveyQuestionRequest questionRequest) {
        if (surveyQuestion.getQuestionType().equals(QuestionType.TEXT) || surveyQuestion.getQuestionType().equals(QuestionType.TRUE_FALSE)) {
            questionRequest.setOptionsRequestList(new ArrayList<>());
        }
    }

    private void handleExceptionThatTheCurrentUserNotAuthorizedToMakeAnyActionForThisQuestion() {
        throw new CustomAuthorizationException(
                LocalizedErrorCodes.SDSS_UNAUTHORIZED_USER_NOT_HAVE_AUTHORITY_TO_MAKE_ANY_ACTION_FOR_SURVEY.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                LocalizedErrorCodes.SDSS_UNAUTHORIZED_USER_NOT_HAVE_AUTHORITY_TO_MAKE_ANY_ACTION_FOR_SURVEY.getErrorCode()
        );
    }
}
