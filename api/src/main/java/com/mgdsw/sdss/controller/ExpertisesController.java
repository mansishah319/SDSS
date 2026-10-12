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
import com.mgdsw.sdss.dto.expertises.ExpertiseFilterRequest;
import com.mgdsw.sdss.dto.expertises.ExpertiseRequest;
import com.mgdsw.sdss.dto.expertises.ExpertisesResponse;
import com.mgdsw.sdss.service.ExpertisesService;
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
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Expertises Controller")
@RequestMapping("/api/v1/expertises")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class ExpertisesController {

    private final ExpertisesService expertisesService;

    @PostMapping
    public ResponseEntity<SuccessResponse<ExpertisesResponse>> createNewExpertises(@Valid @RequestBody ExpertiseRequest expertiseRequest) {
        return ControllerResponseHelper.createResponse(expertisesService.create(expertiseRequest, ExpertisesResponse.class));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<ExpertisesResponse>> update(@RequestParam Integer expertiseId,
                                                                      @Valid @RequestBody ExpertiseRequest expertiseRequest) {
        return ControllerResponseHelper.okResponse(expertisesService.update(expertiseRequest, expertiseId, ExpertisesResponse.class));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<ExpertisesResponse>> findById(@RequestParam Integer expertiseId) {
        return ControllerResponseHelper.okResponse(expertisesService.findById(expertiseId, ExpertisesResponse.class));
    }

    @Operation(summary = "Get all Expertises By Filtration", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PageableAsQueryParam
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<ExpertisesResponse>> filter(ExpertiseFilterRequest searchRequest,
                                                                         @PageableDefault(
                                                                                 size = Integer.MAX_VALUE,
                                                                                 sort = "updatedAt",
                                                                                 direction = Sort.Direction.DESC
                                                                         )
                                                                         @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(expertisesService.filter(searchRequest, pageable));
    }

    @Operation(summary = "Get all Expertises")
    @GetMapping(value = "/all")
    public ResponseEntity<SuccessResponse<List<ExpertisesResponse>>> getAllExpertises() {
        return ControllerResponseHelper.okResponse(expertisesService.getAll(ExpertisesResponse.class));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteExpertiseById(@RequestParam Integer expertiseId) {
        return ControllerResponseHelper.deleteResponse(expertisesService.delete(expertiseId));
    }
}
