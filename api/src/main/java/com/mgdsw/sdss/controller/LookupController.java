/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.lookup.ChallengeLookupCreationScreenResponse;
import com.mgdsw.sdss.dto.lookup.UserListLookupScreenResponse;
import com.mgdsw.sdss.dto.lookup.admin.AdminDashboardResponse;
import com.mgdsw.sdss.dto.lookup.home.MainHomeScreenResponse;
import com.mgdsw.sdss.dto.lookup.home.MainHomeSurveyScreenResponse;
import com.mgdsw.sdss.dto.lookup.user.dashboard.MyMainDashboardResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import com.mgdsw.sdss.service.LookupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Validated
@RestController
@RequestMapping("/api/v1/lookup")
@Tag(name = "Lookup Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class LookupController {

    private final LookupService lookupService;

    @GetMapping(value = "/users/list")
    public ResponseEntity<SuccessResponse<UserListLookupScreenResponse>> getUserListScreenLookupData() {
        return ControllerResponseHelper.okResponse(lookupService.getUserListScreenLookupData());
    }

    @GetMapping(value = "/challenges/creation")
    public ResponseEntity<SuccessResponse<ChallengeLookupCreationScreenResponse>> retrieveChallengeLookupCreationScreenResponse() {
        return ControllerResponseHelper.okResponse(lookupService.retrieveChallengeLookupCreationScreenResponse());
    }

    @GetMapping(value = "/challenges/creation/team-members")
    public ResponseEntity<SuccessResponse<List<UserDropDownListResponse>>> retrieveTeamMembersBasedOnExpertises(@RequestParam(required = false) String expertLevel,
                                                                                                                @RequestParam(required = false) String areaOfExpertise) {
        return ControllerResponseHelper.okResponse(lookupService.retrieveTeamMembersBasedOnExpertises(expertLevel, areaOfExpertise));
    }

    @GetMapping(value = "/admin/dashboard")
    public ResponseEntity<SuccessResponse<AdminDashboardResponse>> retrieveAdminDashboardLookupData() {
        return ControllerResponseHelper.okResponse(lookupService.retrieveAdminDashboardLookupData());
    }

    @GetMapping(value = "/user/my-dashboard")
    public ResponseEntity<SuccessResponse<MyMainDashboardResponse>> myMainDashboard() {
        return ControllerResponseHelper.okResponse(lookupService.myMainDashboard());
    }

    @GetMapping(value = "/main-home-screen-statistics")
    public ResponseEntity<SuccessResponse<MainHomeScreenResponse>> retrieveHomePageStatistics() {
        return ControllerResponseHelper.okResponse(lookupService.retrieveHomePageStatistics());
    }

    @GetMapping(value = "/main-survey-home-screen-statistics")
    public ResponseEntity<SuccessResponse<MainHomeSurveyScreenResponse>> retrieveSurveyHomePageStatistics() {
        return ControllerResponseHelper.okResponse(lookupService.retrieveSurveyHomePageStatistics());
    }
}
