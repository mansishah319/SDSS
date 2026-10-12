/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.surveys.enduser.PublicEndUserSurveyResponse;
import com.mgdsw.sdss.dto.surveys.round.RoundReportMainResponse;
import com.mgdsw.sdss.dto.surveys.round.SdssSurveyRoundRequest;
import com.mgdsw.sdss.dto.surveys.round.SdssSurveyRoundResponse;
import com.mgdsw.sdss.enums.SdssStatus;
import com.mgdsw.sdss.service.SdssSurveyRoundService;
import com.mgdsw.sdss.service.SdssSurveyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Validated
@RestController
@RequestMapping("/api/v1/survey-round")
@Tag(name = "Sdss Survey Round Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class SdssSurveyRoundController {

    private final SdssSurveyRoundService sdssSurveyRoundService;
    private final SdssSurveyService sdssSurveyService;

    @PostMapping
    public ResponseEntity<SuccessResponse<SdssSurveyRoundResponse>> createNewRound(@Valid @RequestBody SdssSurveyRoundRequest request,
                                                                                   @RequestParam Integer surveyId) {
        return ControllerResponseHelper.createResponse(sdssSurveyRoundService.create(request, sdssSurveyService.updateInfoOnRoundSingleCreations(surveyId)));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<SdssSurveyRoundResponse>> updateRound(@Valid @RequestBody SdssSurveyRoundRequest request,
                                                                                @RequestParam Integer surveyId,
                                                                                @RequestParam Integer surveyRoundId) {
        return ControllerResponseHelper.createResponse(sdssSurveyRoundService.update(request, sdssSurveyService.findById(surveyId), surveyRoundId));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<SdssSurveyRoundResponse>> findById(@RequestParam Integer surveyRoundId) {
        return ControllerResponseHelper.okResponse(sdssSurveyRoundService.findById(surveyRoundId, SdssSurveyRoundResponse.class));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteById(@RequestParam Integer surveyRoundId) {
        return ControllerResponseHelper.okResponse(sdssSurveyRoundService.delete(surveyRoundId));
    }

    @GetMapping(value = "/status-list")
    public ResponseEntity<SuccessResponse<List<String>>> retrieveAllStatusBasedOnUserLang() {
        return ControllerResponseHelper.okResponse(SdssStatus.getAllByLanguage());
    }

    @GetMapping(value = "/is-moderator-user")
    public ResponseEntity<SuccessResponse<Boolean>> checkWhoCreatedThisSurveyRound(@RequestParam Integer surveyRoundId) {
        return ControllerResponseHelper.okResponse(sdssSurveyRoundService.checkWhoCreated(surveyRoundId));
    }

    @GetMapping(value = "/preview-questions-form")
    public ResponseEntity<SuccessResponse<PublicEndUserSurveyResponse>> roundPreview(@RequestParam Integer surveyRoundId) {
        return ControllerResponseHelper.okResponse(sdssSurveyService.roundPreview(sdssSurveyRoundService.findById(surveyRoundId)));
    }

    @GetMapping(value = "/retrieve-my-completed-surveys-for-reports")
    public ResponseEntity<SuccessResponse<List<RoundReportMainResponse>>> findAllRoundsCreatedByCurrentUserBasedOnCompletedStatus() {
        return ControllerResponseHelper.okResponse(sdssSurveyRoundService.findAllRoundsBasedOnStatus(Arrays.asList(SdssStatus.COMPLETED, SdssStatus.IN_PROGRESS, SdssStatus.CANCELED)));
    }
}
