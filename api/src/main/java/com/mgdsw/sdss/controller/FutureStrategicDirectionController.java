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
import com.mgdsw.sdss.dto.strategic.directions.StrategicDirectionRequest;
import com.mgdsw.sdss.dto.strategic.directions.StrategicDirectionResponse;
import com.mgdsw.sdss.service.FutureStrategicDirectionService;
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
@Tag(name = "Future Strategic Direction Controller")
@RequestMapping("/api/v1/strategic/future/direction")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class FutureStrategicDirectionController {

    private final FutureStrategicDirectionService strategicDirectionService;

    @Operation(summary = "Create new Strategic Direction", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @PostMapping
    public ResponseEntity<SuccessResponse<StrategicDirectionResponse>> createNewStrategicDirection(@Valid @RequestBody StrategicDirectionRequest request) {
        return ControllerResponseHelper.createResponse(strategicDirectionService.create(request, StrategicDirectionResponse.class));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<StrategicDirectionResponse>> update(@RequestParam Integer strategicDirectionId,
                                                                              @Valid @RequestBody StrategicDirectionRequest strategicDirectionRequest) {
        return ControllerResponseHelper.okResponse(strategicDirectionService.update(strategicDirectionRequest, strategicDirectionId, StrategicDirectionResponse.class));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<StrategicDirectionResponse>> findById(@RequestParam Integer strategicDirectionId) {
        return ControllerResponseHelper.okResponse(strategicDirectionService.findById(strategicDirectionId, StrategicDirectionResponse.class));
    }

    @Operation(summary = "Get all Strategic Directions", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PageableAsQueryParam
    @GetMapping("/all-pagination")
    public ResponseEntity<PaginationResponse<StrategicDirectionResponse>> getAll(
            @PageableDefault(
                    size = Integer.MAX_VALUE,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC
            )
            @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(strategicDirectionService.getAllWithPagination(pageable, StrategicDirectionResponse.class));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteById(@RequestParam Integer strategicDirectionId) {
        return ControllerResponseHelper.deleteResponse(strategicDirectionService.delete(strategicDirectionId));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<SuccessResponse<List<StrategicDirectionResponse>>> retrieveDropdownList() {
        return ControllerResponseHelper.okResponse(strategicDirectionService.getAll(StrategicDirectionResponse.class));
    }
}
