/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.roles.CreateRoleRequest;
import com.mgdsw.sdss.dto.roles.RolesResponse;
import com.mgdsw.sdss.service.RolesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.UNDER_SCORE;
import static com.mgdsw.sdss.util.DpRolesUtil.ADMIN_MODERATOR_PREAUTHORIZE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Validated
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class RolesController {

    private final RolesService rolesService;

    @Operation(summary = "Add new Role")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
//    @PreAuthorize("hasAnyRole('ADMIN') or hasAnyPermission('READ','UPDATE','WRITE')")
    @PostMapping
    public ResponseEntity<SuccessResponse<RolesResponse>> createNewRole(@Valid @RequestBody CreateRoleRequest request) {
        return ControllerResponseHelper.createResponse(rolesService.createNewRole(request));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<RolesResponse>>> getAllRoles() {
        return ControllerResponseHelper.okResponse(rolesService.getAll(RolesResponse.class));
    }

    @Operation(summary = "Update Role")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @PutMapping(value = "/{roleId}")
    public ResponseEntity<SuccessResponse<RolesResponse>> updateRole(@PathVariable Integer roleId, @Valid @RequestBody CreateRoleRequest request) {
        request.setNameEn(request.getNameEn().toUpperCase().replace(SPACE, UNDER_SCORE));
        return ControllerResponseHelper.createResponse(rolesService.update(request, roleId, RolesResponse.class));
    }
}
