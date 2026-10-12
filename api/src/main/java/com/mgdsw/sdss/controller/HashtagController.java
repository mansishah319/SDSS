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
import com.mgdsw.sdss.dto.hashtag.HashtagCreationRequest;
import com.mgdsw.sdss.dto.hashtag.HashtagCreationResponse;
import com.mgdsw.sdss.service.HashtagService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@Tag(name = "Hashtag Controller")
@RequestMapping("/api/v1/hashtag")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class HashtagController {

    private final HashtagService hashtagService;


    @Operation(summary = "Create new Hashtag", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @PostMapping
    public ResponseEntity<SuccessResponse<HashtagCreationResponse>> createNewStrategicDirection(@Valid @RequestBody HashtagCreationRequest request) {
        return ControllerResponseHelper.createResponse(hashtagService.create(request));
    }

    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Get all Hashtags", security = {@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)})
    @PageableAsQueryParam
    @GetMapping
    public ResponseEntity<PaginationResponse<HashtagCreationResponse>> retrieveAllHashtags(
            @PageableDefault(
                    size = Integer.MAX_VALUE,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC
            )
            @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(hashtagService.getAllWithPagination(pageable, HashtagCreationResponse.class));
    }
}
