/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.surveys.questions.ReportSurveyQuestionAnalysis;
import com.mgdsw.sdss.dto.surveys.user.InvitedUserSingleAnswerRequest;
import com.mgdsw.sdss.dto.surveys.user.SdssSurveyUserQuestionAnswersResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUserQuestionAnswers;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammed Abdu
 * @version vr0.
 * @createdOn Oct, 2024
 */
public interface SdssSurveyUserQuestionAnswersService extends BaseService<Integer, SdssSurveyUserQuestionAnswers> {

    List<SdssSurveyUserQuestionAnswersResponse> createNewUserQuestionAnswers(List<InvitedUserSingleAnswerRequest> requestList, SdssSurveyUsers invitedUser);

    List<InvitedUserSingleAnswerRequest> retrieveCurrentActiveRoundUserAnswers(SdssSurveyUsers invitedUser, List<SdssSurveyQuestion> questions);

    List<SdssSurveyUserQuestionAnswersResponse> retrieveCurrentUserQuestionAnswersBasedOnSurveyRound(SdssSurveyUsers invitedUser, List<SdssSurveyQuestion> questions);

    Map<String, Serializable> getStatisticsByQuestionIdAndTypeForFinalReport(SdssSurveyQuestion question);

    Boolean checkIfUserHasAnswered(Integer userId);

    List<SdssSurveyUserQuestionAnswers> retrieveAllAnswersForAllUsersByRoundId(Integer roundId);

    List<SdssSurveyUserQuestionAnswersResponse> retrieveAllDetailsForNonZeroProbabilityByQuestion(SdssSurveyQuestion question);

    List<SdssSurveyUserQuestionAnswersResponse> retrieveAllDetailsForNonZeroImpactByQuestion(SdssSurveyQuestion question);

    ReportSurveyQuestionAnalysis generateReportSurveyQuestionAnalysis(SdssSurveyQuestion question);

    List<SdssSurveyUserQuestionAnswers> retrieveAllAnswersForAllUsersByQuestion(SdssSurveyQuestion question);
}
