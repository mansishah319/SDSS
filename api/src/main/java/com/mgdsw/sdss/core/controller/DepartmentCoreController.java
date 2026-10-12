/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.core.dto.DepartmentCoreResponse;
import com.mgdsw.sdss.core.dto.SubDepartmentCoreResponse;
import com.mgdsw.sdss.core.service.DepartmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;
import static com.mgdsw.sdss.util.DpRolesUtil.ADMIN_MODERATOR_PREAUTHORIZE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Apr, 2024
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Core Department Controller")
@RequestMapping("/api/v1/core/department")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class DepartmentCoreController {

    private final DepartmentService employeeService;

    @GetMapping("/sectors")
    public ResponseEntity<SuccessResponse<List<DepartmentCoreResponse>>> retrieveAllSectors() {
        return ControllerResponseHelper.okResponse(employeeService.retrieveAllSectors());
    }

    @GetMapping("/all")
    public ResponseEntity<SuccessResponse<List<DepartmentCoreResponse>>> retrieveAllDepartments() {
        return ControllerResponseHelper.okResponse(employeeService.getAllDepartments());
    }

    @GetMapping("/all-without-restrictions")
    public ResponseEntity<SuccessResponse<List<DepartmentCoreResponse>>> retrieveAllDepartmentsWithoutRestriction() {
        return ControllerResponseHelper.okResponse(employeeService.getAllDepartmentsWithoutRestriction());
    }

    @GetMapping("/sub-departments")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    public ResponseEntity<SuccessResponse<List<SubDepartmentCoreResponse>>> retrieveAllSubDepartmentsByDepartmentId(@RequestParam Long departmentId) {
        return ControllerResponseHelper.okResponse(employeeService.getAllSubDepartmentsByDepartmentId(departmentId));
    }


    @GetMapping("/sections")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    public ResponseEntity<SuccessResponse<List<SubDepartmentCoreResponse>>> findAllSectionBySubDepartmentId(@RequestParam Long subDepartmentId) {
        return ControllerResponseHelper.okResponse(employeeService.findAllSectionBySubDepartmentId(subDepartmentId));
    }

    @GetMapping("/multi-sub-departments")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    public ResponseEntity<SuccessResponse<List<SubDepartmentCoreResponse>>> retrieveAllSubDepartmentsByDepartmentIdList(@RequestParam List<Long> departmentIdList) {
        return ControllerResponseHelper.okResponse(employeeService.getAllSubDepartmentsByDepartmentIdList(departmentIdList));
    }
}
