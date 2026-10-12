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
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsRequest;
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsResponse;
import com.mgdsw.sdss.service.StrategicGoalsService;
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

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;
import static com.mgdsw.sdss.util.DpRolesUtil.ADMIN_MODERATOR_PREAUTHORIZE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "StrategicGoals Controller")
@RequestMapping("/api/v1/strategic/goals")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class StrategicGoalsController {

    private final StrategicGoalsService strategicGoalsService;

    @Operation(summary = "Create new StrategicGoals", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @PostMapping
    public ResponseEntity<SuccessResponse<StrategicGoalsResponse>> createNewStrategicGoal(@Valid @RequestBody StrategicGoalsRequest request) {
        return ControllerResponseHelper.createResponse(strategicGoalsService.createNewStrategicGoal(request));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<StrategicGoalsResponse>> update(@RequestParam Integer strategicGoalsId,
                                                                          @Valid @RequestBody StrategicGoalsRequest strategicGoalsRequest) {
        return ControllerResponseHelper.okResponse(strategicGoalsService.update(strategicGoalsId, strategicGoalsRequest));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<StrategicGoalsResponse>> findById(@RequestParam Integer strategicGoalsId) {
        return ControllerResponseHelper.okResponse(strategicGoalsService.getById(strategicGoalsId));
    }

    @Operation(summary = "Get all Strategic Goals", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PageableAsQueryParam
    @GetMapping("/all")
    public ResponseEntity<PaginationResponse<StrategicGoalsResponse>> getAll(
            @PageableDefault(
                    size = Integer.MAX_VALUE,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC
            )
            @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(strategicGoalsService.getAll(pageable));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteById(@RequestParam Integer strategicGoalsId) {
        return ControllerResponseHelper.deleteResponse(strategicGoalsService.delete(strategicGoalsId));
    }
}
