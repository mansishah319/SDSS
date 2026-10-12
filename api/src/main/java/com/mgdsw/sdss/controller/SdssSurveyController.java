/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.surveys.SdssSurveyCreationRequest;
import com.mgdsw.sdss.dto.surveys.SdssSurveyResponse;
import com.mgdsw.sdss.dto.surveys.SdssSurveySearch;
import com.mgdsw.sdss.dto.surveys.SurveyInvitationEmailSendResultResponse;
import com.mgdsw.sdss.dto.surveys.SurveyInvitationEmailSendSummaryResponse;
import com.mgdsw.sdss.dto.surveys.UpdateSdssSurveyStatus;
import com.mgdsw.sdss.dto.surveys.enduser.PublicEndUserSurveyResponse;
import com.mgdsw.sdss.dto.surveys.user.SurveyUserDashboardResponse;
import com.mgdsw.sdss.enums.SurveyStatus;
import com.mgdsw.sdss.service.SdssSurveyService;
import com.mgdsw.sdss.specification.SdssSurveySpecification;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;
import static com.mgdsw.sdss.util.DpRolesUtil.ADMIN_MODERATOR_PREAUTHORIZE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Validated
@RestController
@RequestMapping("/api/v1/sdss-survey")
@Tag(name = "Sdss Survey Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class SdssSurveyController {

    private final SdssSurveyService surveyService;

    @PostMapping
    public ResponseEntity<SuccessResponse<SdssSurveyResponse>> create(@Valid @RequestBody SdssSurveyCreationRequest request) {
        return ControllerResponseHelper.createResponse(surveyService.create(request));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<SdssSurveyResponse>> update(@Valid @RequestBody SdssSurveyCreationRequest request, @RequestParam Integer surveyId) {
        return ControllerResponseHelper.okResponse(surveyService.update(request, surveyId));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<SdssSurveyResponse>> findById(@RequestParam Integer surveyId) {
        return ControllerResponseHelper.okResponse(surveyService.findSurveyDetailsById(surveyId));
    }

    @PutMapping(value = "/resend-invitation-email")
    public ResponseEntity<SuccessResponse<SurveyInvitationEmailSendResultResponse>> resendInvitationEmail(@RequestParam Integer surveyId, @RequestParam Long employeeGrpNumber) {
        return ControllerResponseHelper.okResponse(surveyService.resendInvitationEmail(surveyId, employeeGrpNumber));
    }

    @PutMapping(value = "/resend-missing-invitation-emails")
    public ResponseEntity<SuccessResponse<SurveyInvitationEmailSendSummaryResponse>> resendMissingInvitationEmails(@RequestParam Integer surveyId) {
        return ControllerResponseHelper.okResponse(surveyService.resendMissingInvitationEmails(surveyId));
    }

    @PutMapping(value = "/update-status")
    public ResponseEntity<SuccessResponse<SdssSurveyResponse>> updateStatus(@Valid @RequestBody UpdateSdssSurveyStatus request) {
        return ControllerResponseHelper.createResponse(surveyService.updateStatus(request));
    }

    @PutMapping(value = "/active-round-status")
    public ResponseEntity<SuccessResponse<SdssSurveyResponse>> activeRoundStatus(@RequestParam Integer surveyId, @RequestParam Integer roundNumber) {
        return ControllerResponseHelper.createResponse(surveyService.activeRoundStatus(surveyId, roundNumber));
    }

    @PutMapping(value = "/completed-round-status")
    public ResponseEntity<SuccessResponse<SdssSurveyResponse>> completedRoundStatus(@RequestParam Integer surveyId, @RequestParam Integer roundNumber) {
        return ControllerResponseHelper.createResponse(surveyService.completedRoundStatus(surveyId, roundNumber));
    }

    @GetMapping(value = "/status-list")
    public ResponseEntity<SuccessResponse<List<String>>> retrieveAllStatusBasedOnUserLang() {
        return ControllerResponseHelper.okResponse(SurveyStatus.getAllByLanguage());
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteById(@RequestParam Integer surveyId) {
        return ControllerResponseHelper.deleteResponse(surveyService.deleteById(surveyId));
    }

    @GetMapping(value = "/is-user-allowed")
    public ResponseEntity<SuccessResponse<Boolean>> checkWhoCreatedThisSurvey(@RequestParam Integer surveyId) {
        return ControllerResponseHelper.okResponse(surveyService.checkWhoCreated(surveyId));
    }

    @GetMapping(value = "/public/dashboard")
    public ResponseEntity<SuccessResponse<SurveyUserDashboardResponse>> retrieveSurveyDashboard(@RequestHeader(name = "SurveyAuthorization", required = false) String userInfoKey) {
        return ControllerResponseHelper.okResponse(surveyService.retrieveUserDashboard(userInfoKey));
    }

    @GetMapping(value = "/public/retrieve-my-invited-survey")
    public ResponseEntity<SuccessResponse<PublicEndUserSurveyResponse>> retrieveMyCurrentActiveSurveyRound(@RequestParam Integer surveyId,
                                                                                                           @RequestHeader(name = "SurveyAuthorization", required = false) String userInfoKey) {
        return ControllerResponseHelper.okResponse(surveyService.retrieveMyCurrentActiveSurveyRound(surveyId, userInfoKey));
    }

    @PageableAsQueryParam
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @GetMapping(value = "/search")
    public ResponseEntity<PaginationResponse<SdssSurveyResponse>> filter(SdssSurveySearch search,
                                                                         @PageableDefault(
                                                                                 size = Integer.MAX_VALUE,
                                                                                 sort = "id",
                                                                                 direction = Sort.Direction.DESC
                                                                         )
                                                                         @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(surveyService.filterSurveyInfo(new SdssSurveySpecification(search), pageable));
    }
}
