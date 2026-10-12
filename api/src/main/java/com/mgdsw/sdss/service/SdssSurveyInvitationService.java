/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.surveys.SurveyInvitationEmailSendResultResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyInvitation;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */
public interface SdssSurveyInvitationService extends BaseService<Integer, SdssSurveyInvitation> {

    void initInvitation(TemplatesTypeKeys templatesTypeKey, SdssSurvey survey, SdssSurveyRound surveyRound);

    void sendManualInvitation(SdssSurvey survey, SdssSurveyRound sdssSurveyRound);

    SurveyInvitationEmailSendResultResponse sendManualInvitationForUser(SdssSurvey survey, SdssSurveyRound sdssSurveyRound, Long grpNumber);

    void sendManualInvitation(Integer surveyId, Integer roundId, String baseUrl);

    SdssSurveyInvitation checkUserExistenceForAttendingThisSurvey(SdssSurveyRound surveyRound, String userInfoKey);

    List<SdssSurvey> retrieveMyCurrentLiveSurveys(String userInfoKey);

    List<SdssSurveyRound> retrieveMyCurrentHistorySurveys(String userInfoKey);

    SdssSurveyInvitation checkUserExistenceForAttendingSurvey(SdssSurvey survey, String userInfoKey);

    long countDistinctInvitedUsersBySurveyAndSurveyRound(SdssSurvey survey, SdssSurveyRound surveyRound);
}
