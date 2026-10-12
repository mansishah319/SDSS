/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.dto.surveys.user.ReportUserSurveyAnswerStatusResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.
 * @email eng.mo.abdu@gmail.com
 * @creationDate 26 Nov 2024
 */
public interface SdssSurveyUsersService extends BaseService<Integer, SdssSurveyUsers> {

    SdssSurveyUsers createInvitedUserInfo(Long employeeGrpNumber);

    EmployeeInfoKeyResponse retrieveEmployeeInfoKeyResponse(Long grpNumber);

    SdssSurveyUsers retrieveByGrpNumber(Long grpNumber);

    String loginSurveyAccessKey(String existUserToken, Long grpNumber, String accessKey);

    SdssSurveyUsers retrieveByUserInfoKey(String userInfoKey);

    List<ReportUserSurveyAnswerStatusResponse> getUsersWithAnswerStatus(SdssSurveyRound surveyRound);

    long countSurveyUsers();

    SdssSurveyUsers retrieveByGrpNumberOrNull(Long grpNumber);
}
