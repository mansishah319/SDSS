/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.core.dto.lookup.DropDownListLookup;
import com.mgdsw.sdss.core.service.JobsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
@Tag(name = "Core Jobs Controller")
@RequestMapping("/api/v1/core/jobs")
public class JobsController {

    private final JobsService jobsService;

    @Operation(summary = "Get Job By ID")
    @GetMapping
    public ResponseEntity<SuccessResponse<DropDownListLookup>> findResponseById(@RequestParam Integer jobId) {
        return ControllerResponseHelper.okResponse(jobsService.findResponseById(jobId));
    }

    @Operation(summary = "Get all Jobs Pageable")
    @PageableAsQueryParam
    @GetMapping(value = "/all-pageable")
    public ResponseEntity<PaginationResponse<DropDownListLookup>> findAllJobs(@PageableDefault(size = Integer.MAX_VALUE)
                                                                              @Parameter(hidden = true) Pageable pageable,
                                                                              @RequestParam(required = false) String search) {
        return ResponseEntity.ok(jobsService.retrieveAll(pageable, search));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<SuccessResponse<List<DropDownListLookup>>> findAllJobs() {
        return ControllerResponseHelper.okResponse(jobsService.retrieveAll());
    }
}
