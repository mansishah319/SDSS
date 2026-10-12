/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.audits.AuditsResponse;
import com.mgdsw.sdss.dto.audits.AuditsSearchRequest;
import com.mgdsw.sdss.dto.audits.CreateAuditsRequest;
import com.mgdsw.sdss.service.AuditsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Validated
@RestController
@RequestMapping("/api/v1/audits")
@Tag(name = "Audits Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class AuditsController {


    private final AuditsService auditsService;

    @Operation(summary = "Add new Audits")
    @PostMapping
    public ResponseEntity<SuccessResponse<AuditsResponse>> createNewAudits(@Valid @RequestBody CreateAuditsRequest createAuditsRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.<AuditsResponse>builder()
                        .data(auditsService.createNewAudits(createAuditsRequest))
                        .message(DpConstants.Messages.SuccessMessages.CREATED_SUCCESSFULLY)
                        .build());
    }

    @Operation(summary = "Get a Audit by auditId")
    @GetMapping("/{auditId}")
    public ResponseEntity<SuccessResponse<AuditsResponse>> findAuditsByID(@Parameter(name = "auditId", in = ParameterIn.PATH, required = true)
                                                                          @PathVariable Integer auditId) {
        return ResponseEntity.ok(SuccessResponse.<AuditsResponse>builder()
                .data(auditsService.findAuditsByID(auditId))
                .message(DpConstants.Messages.SuccessMessages.DATA_RETRIEVED_SUCCESSFULLY)
                .build());
    }

    @Operation(summary = "Update Audit by ID", description = "Updates an existing Audit by its ID.")
    @ApiResponse(responseCode = "200", description = "Audits updated successfully",
            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "Audit not found")
    @PutMapping("/{auditId}")
    public ResponseEntity<SuccessResponse<AuditsResponse>> updateAudits(@RequestBody CreateAuditsRequest createAuditsRequest,
                                                                        @Parameter(name = "auditId", in = ParameterIn.PATH, required = true)
                                                                        @PathVariable Integer auditId) {
        return ResponseEntity.ok(SuccessResponse.<AuditsResponse>builder()
                .data(auditsService.updateAudits(createAuditsRequest, auditId))
                .message(DpConstants.Messages.SuccessMessages.UPDATED_SUCCESSFULLY)
                .build());
    }

    @Operation(summary = "Get all Audits")
    @PageableAsQueryParam
    @GetMapping
    public ResponseEntity<PaginationResponse<AuditsResponse>> findAllAudits(@PageableDefault(size = 20, page = 1)
                                                                            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(auditsService.findAllAudits(pageable));
    }

    @Operation(summary = "Get all Audits By Filtration")
    @PageableAsQueryParam
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<AuditsResponse>> auditsFiltration(@RequestBody AuditsSearchRequest search,
                                                                               @PageableDefault(size = 20, page = 1)
                                                                               @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(auditsService.auditsFiltration(search, pageable));
    }
}
