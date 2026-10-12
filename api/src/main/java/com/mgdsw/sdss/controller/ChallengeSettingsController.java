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
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsLangResponse;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsRequest;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsResponse;
import com.mgdsw.sdss.dto.challenge.settings.SettingsTypeResponse;
import com.mgdsw.sdss.enums.ChallengeSettingType;
import com.mgdsw.sdss.service.ChallengeSettingsService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
 * @createdOn Nov, 2023
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Challenge Settings Controller")
@RequestMapping("/api/v1/challenges/settings")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class ChallengeSettingsController {

    private final ChallengeSettingsService challengeSettingsService;

    @Operation(summary = "Create new Challenge Setting", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @PostMapping
    public ResponseEntity<SuccessResponse<ChallengeSettingsResponse>> createNewChallengeSetting(@Valid @RequestBody ChallengeSettingsRequest request) {
        return ControllerResponseHelper.createResponse(challengeSettingsService.create(request, ChallengeSettingsResponse.class));
    }

    @Operation(summary = "Update Challenge Setting", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @PutMapping
    public ResponseEntity<SuccessResponse<ChallengeSettingsResponse>> updateChallengeSetting(@RequestParam Integer challengeSettingsId,
                                                                                             @Valid @RequestBody ChallengeSettingsRequest request) {
        return ControllerResponseHelper.createResponse(challengeSettingsService.update(request, challengeSettingsId, ChallengeSettingsResponse.class));
    }


    @Operation(summary = "Get Challenge Setting By ID", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @GetMapping
    public ResponseEntity<SuccessResponse<ChallengeSettingsResponse>> findById(@RequestParam Integer challengeSettingsId) {
        return ControllerResponseHelper.createResponse(challengeSettingsService.findById(challengeSettingsId, ChallengeSettingsResponse.class));
    }


    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Get all Challenge Settings", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PageableAsQueryParam
    @GetMapping(value = "/all")
    public ResponseEntity<PaginationResponse<ChallengeSettingsResponse>> retrieveAllStrategicDirections(
            @PageableDefault(
                    size = Integer.MAX_VALUE,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC
            )
            @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(challengeSettingsService.getAllWithPagination(pageable, ChallengeSettingsResponse.class));
    }

    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Get all Challenge Settings based on type", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @GetMapping(value = "/retrieve-by-type")
    public ResponseEntity<SuccessResponse<List<ChallengeSettingsLangResponse>>> findAllByType(@RequestParam ChallengeSettingType challengeSettingType) {
        return ControllerResponseHelper.okResponse(challengeSettingsService.findAllByType(challengeSettingType));
    }

    @GetMapping(value = "/types")
    public ResponseEntity<SuccessResponse<List<SettingsTypeResponse>>> findAllChallengeSettingsTypes() {
        return ControllerResponseHelper.okResponse(challengeSettingsService.findAllChallengeSettingsTypes());
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteChallengeSettingById(@RequestParam Integer challengeSettingsId) {
        return ControllerResponseHelper.deleteResponse(challengeSettingsService.delete(challengeSettingsId));
    }
}
