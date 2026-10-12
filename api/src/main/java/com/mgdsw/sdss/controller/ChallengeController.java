/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeAdminDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeAttendeesResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeSearchRequest;
import com.mgdsw.sdss.dto.challenge.ChallengesCreationRequest;
import com.mgdsw.sdss.dto.challenge.ChallengesResponse;
import com.mgdsw.sdss.dto.challenge.SimpleChallengesResponse;
import com.mgdsw.sdss.enums.ChallengeStatus;
import com.mgdsw.sdss.enums.SurveyStatus;
import com.mgdsw.sdss.service.ChallengesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;
import static com.mgdsw.sdss.util.DpRolesUtil.ADMIN_MODERATOR_PREAUTHORIZE;
import static com.mgdsw.sdss.util.DpRolesUtil.CHALLENGES_ADMIN_VALIDATOR_MODERATOR_PREAUTHORIZE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
@Tag(name = "Challenges Controller")
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    private final ChallengesService challengesService;

    @Operation(summary = "Create new Challenge")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ChallengesResponse>> create(
            @Valid @RequestPart("request") ChallengesCreationRequest challengesCreationRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ControllerResponseHelper.createResponse(challengesService.create(challengesCreationRequest, files));
    }

    @Operation(summary = "Update Challenge")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ChallengesResponse>> update(@RequestParam Integer challengeId,
                                                                      @Valid @RequestPart("request") ChallengesCreationRequest request,
                                                                      @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ControllerResponseHelper.createResponse(challengesService.update(challengeId, request, files));
    }

    @Operation(summary = "Delete Challenge By ID")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @DeleteMapping(value = "/{challengeId}")
    public ResponseEntity<SuccessResponse<String>> delete(@PathVariable Integer challengeId) {
        return ControllerResponseHelper.okResponse(challengesService.delete(challengeId));
    }

    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Get Challenges By ID")
    @GetMapping("/{challengeId}")
    public ResponseEntity<SuccessResponse<ChallengesResponse>> findById(@PathVariable Integer challengeId) {
        return ControllerResponseHelper.okResponse(challengesService.viewChallengeScreen(challengeId));
    }

    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Get all Challenges By Filtration")
    @PageableAsQueryParam
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<ChallengesResponse>> filter(ChallengeSearchRequest searchRequest,
                                                                         @PageableDefault(
                                                                                 size = Integer.MAX_VALUE,
                                                                                 sort = "updatedAt",
                                                                                 direction = Sort.Direction.DESC
                                                                         )
                                                                         @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(challengesService.filter(searchRequest, pageable));
    }

    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @GetMapping(value = "/user-attendees")
    public ResponseEntity<SuccessResponse<ChallengeAttendeesResponse>> retrieveAllUsersInvitedToThisChallengeId(@RequestParam Integer challengeId) {
        return ControllerResponseHelper.okResponse(challengesService.retrieveAllUsersInvitedToThisChallengeId(challengeId));
    }

    @GetMapping(value = "/dashboard")
    public ResponseEntity<SuccessResponse<ChallengeDashboardResponse>> retrieveChallengeDashboardResponse(@RequestParam Integer challengeId) {
        return ControllerResponseHelper.okResponse(challengesService.retrieveChallengeDashboardResponse(challengeId));
    }

    @PutMapping(value = "/change-survey-status")
    public ResponseEntity<SuccessResponse<ChallengesResponse>> updateChallengeSurveyStatus(@RequestParam Integer challengeId,
                                                                                           @RequestParam SurveyStatus surveyStatus) {
        return ControllerResponseHelper.okResponse(challengesService.updateChallengeSurveyStatus(challengeId, surveyStatus));
    }

    @PreAuthorize(CHALLENGES_ADMIN_VALIDATOR_MODERATOR_PREAUTHORIZE)
    @PutMapping(value = "/change-status")
    public ResponseEntity<SuccessResponse<SimpleChallengesResponse>> updateChallengeStatus(@RequestParam Integer challengeId,
                                                                                           @RequestParam ChallengeStatus status) {
        return ControllerResponseHelper.okResponse(challengesService.updateChallengeStatus(challengeId, status));
    }

    @GetMapping(value = "/retrieve-by-status")
    public ResponseEntity<SuccessResponse<List<ChallengeAdminDashboardResponse>>> retrieveAllChallengesBasedOnStatus(@RequestParam ChallengeStatus status) {
        return ControllerResponseHelper.okResponse(challengesService.retrieveAllChallengesBasedOnStatus(status));
    }

    @Operation(summary = "Get all Challenges By Filtration for current invited Users")
    @PageableAsQueryParam
    @GetMapping("/my-challenges")
    public ResponseEntity<PaginationResponse<ChallengesResponse>> filterBasedOnInvitedUsers(ChallengeSearchRequest searchRequest,
                                                                                            @PageableDefault(size = Integer.MAX_VALUE, page = 1)
                                                                                            @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(challengesService.filterBasedOnInvitedUsers(searchRequest, pageable));
    }

    @GetMapping(value = "/send-welcome-voting-email")
    public ResponseEntity<SuccessResponse<String>> welcomeReminder(@RequestParam Integer challengeId) {
        return ControllerResponseHelper.okResponse(challengesService.welcomeStartVotingEmail(challengeId));
    }

    @GetMapping(value = "/send-validator-reminder")
    public ResponseEntity<SuccessResponse<String>> validatorStartValidateNewChallengeEmail(@RequestParam Integer challengeId) {
        return ControllerResponseHelper.okResponse(challengesService.validatorStartValidateNewChallengeEmail(challengeId));
    }

    @GetMapping(value = "/status-list")
    public ResponseEntity<SuccessResponse<List<String>>> retrieveAllStatusBasedOnUserLang() {
        return ControllerResponseHelper.okResponse(ChallengeStatus.getAllByLanguage());
    }

    @GetMapping(value = "/details-pdf", produces = MediaType.APPLICATION_PDF_VALUE + ";charset=UTF-8")
    public ResponseEntity<byte[]> generateChallengeReportPDF(@RequestParam Integer challengeId) {
        // Generate a meaningful filename (you can customize this)
        String fileName = "challenge-report-round-" + challengeId + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .body(challengesService.generateChallengeReportPDF(challengeId));
    }
}
