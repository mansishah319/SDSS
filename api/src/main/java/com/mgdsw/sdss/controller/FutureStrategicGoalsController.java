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
import com.mgdsw.sdss.dto.strategic.goals.FutureStrategicGoalsRequest;
import com.mgdsw.sdss.dto.strategic.goals.FutureStrategicGoalsResponse;
import com.mgdsw.sdss.service.FutureStrategicGoalsService;
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
@Tag(name = "Future Strategic Goals Controller")
@RequestMapping("/api/v1/strategic/future/goals")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class FutureStrategicGoalsController {

    private final FutureStrategicGoalsService strategicGoalsService;

    @Operation(summary = "Create new Future StrategicGoals", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @PostMapping
    public ResponseEntity<SuccessResponse<FutureStrategicGoalsResponse>> createNewStrategicGoal(@Valid @RequestBody FutureStrategicGoalsRequest request) {
        return ControllerResponseHelper.createResponse(strategicGoalsService.createNewFutureStrategicGoal(request));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<FutureStrategicGoalsResponse>> update(@RequestParam Integer strategicGoalsId,
                                                                                @Valid @RequestBody FutureStrategicGoalsRequest strategicGoalsRequest) {
        return ControllerResponseHelper.okResponse(strategicGoalsService.update(strategicGoalsId, strategicGoalsRequest));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<FutureStrategicGoalsResponse>> findById(@RequestParam Integer strategicGoalsId) {
        return ControllerResponseHelper.okResponse(strategicGoalsService.findById(strategicGoalsId, FutureStrategicGoalsResponse.class));
    }

    @Operation(summary = "Get all Future Strategic Goals", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PageableAsQueryParam
    @GetMapping("/all")
    public ResponseEntity<PaginationResponse<FutureStrategicGoalsResponse>> getAll(
            @PageableDefault(
                    size = Integer.MAX_VALUE,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC
            )
            @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(strategicGoalsService.getAllWithPagination(pageable, FutureStrategicGoalsResponse.class));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteById(@RequestParam Integer strategicGoalsId) {
        return ControllerResponseHelper.deleteResponse(strategicGoalsService.delete(strategicGoalsId));
    }
}
