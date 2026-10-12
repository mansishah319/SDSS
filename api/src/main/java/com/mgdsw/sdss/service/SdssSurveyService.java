/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.dto.surveys.SdssSurveyCreationRequest;
import com.mgdsw.sdss.dto.surveys.SdssSurveyResponse;
import com.mgdsw.sdss.dto.surveys.SurveyInvitationEmailSendResultResponse;
import com.mgdsw.sdss.dto.surveys.SurveyInvitationEmailSendSummaryResponse;
import com.mgdsw.sdss.dto.surveys.UpdateSdssSurveyStatus;
import com.mgdsw.sdss.dto.surveys.enduser.PublicEndUserSurveyResponse;
import com.mgdsw.sdss.dto.surveys.user.SurveyUserDashboardResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.specification.SdssSurveySpecification;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.
 * @createdOn Oct, 2024
 */
public interface SdssSurveyService extends BaseService<Integer, SdssSurvey> {

    SdssSurveyResponse create(SdssSurveyCreationRequest request);

    SdssSurveyResponse updateStatus(UpdateSdssSurveyStatus updateSdssSurveyStatus);

    SdssSurveyResponse activeRoundStatus(Integer surveyId, Integer roundNumber);

    SdssSurveyResponse completedRoundStatus(Integer surveyId, Integer roundNumber);

    SdssSurveyResponse update(SdssSurveyCreationRequest request, Integer surveyId);

    SdssSurveyResponse findSurveyDetailsById(Integer surveyId);

    SurveyInvitationEmailSendResultResponse resendInvitationEmail(Integer surveyId, Long employeeGrpNumber);

    SurveyInvitationEmailSendSummaryResponse resendMissingInvitationEmails(Integer surveyId);

    SdssSurvey updateInfoOnRoundSingleCreations(Integer surveyId);

    String deleteById(Integer surveyId);

    PaginationResponse<SdssSurveyResponse> filterSurveyInfo(SdssSurveySpecification sdssSurveySpecification, Pageable pageable);

    PublicEndUserSurveyResponse retrieveMyCurrentActiveSurveyRound(Integer surveyId, String userInfoKey);

    List<PublicEndUserSurveyResponse> retrieveMyCurrentLiveSurveys(String userInfoKey);

    PublicEndUserSurveyResponse roundPreview(SdssSurveyRound sdssSurveyRound);

    List<PublicEndUserSurveyResponse> retrieveAllUserSurveyHistory(String userInfoKey);

    long countAllSurveysExistOnTheSystem();

    SurveyUserDashboardResponse retrieveUserDashboard(String userInfoKey);
}
