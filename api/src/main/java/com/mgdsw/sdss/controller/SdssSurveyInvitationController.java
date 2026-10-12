/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.surveys.enduser.PublicEndUserSurveyResponse;
import com.mgdsw.sdss.dto.surveys.user.SurveyUserDashboardResponse;
import com.mgdsw.sdss.service.SdssSurveyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 26 Nov 2024
 */

@Validated
@RestController
@RequestMapping("/v1/public/api/v1/invited-user-survey-profile")
@Tag(name = "Sdss Invited User Survey Profile Controller")
@RequiredArgsConstructor
public class SdssSurveyInvitationController {

    private final SdssSurveyService surveyService;

    @GetMapping(value = "/my-dashboard")
    public ResponseEntity<SuccessResponse<SurveyUserDashboardResponse>> retrieveMyDashboard(@RequestHeader(name = "SurveyAuthorization", required = false) String userInfoKey) {
        return ControllerResponseHelper.okResponse(surveyService.retrieveUserDashboard(userInfoKey));
    }

    @GetMapping(value = "/my-live-surveys")
    public ResponseEntity<SuccessResponse<List<PublicEndUserSurveyResponse>>> retrieveMyCurrentLiveSurveys(@RequestHeader(name = "SurveyAuthorization", required = false) String userInfoKey) {
        return ControllerResponseHelper.okResponse(surveyService.retrieveMyCurrentLiveSurveys(userInfoKey));
    }

    @GetMapping(value = "/my-surveys-history")
    public ResponseEntity<SuccessResponse<List<PublicEndUserSurveyResponse>>> retrieveAllUserSurveyHistory(@RequestHeader(name = "SurveyAuthorization", required = false) String userInfoKey) {
        return ControllerResponseHelper.okResponse(surveyService.retrieveAllUserSurveyHistory(userInfoKey));
    }
}
