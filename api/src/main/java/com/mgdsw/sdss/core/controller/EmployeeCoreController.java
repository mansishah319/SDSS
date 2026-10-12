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
import com.mgdsw.sdss.core.dto.EmployeeAudienceSearchRequest;
import com.mgdsw.sdss.core.dto.EmployeeDetailsResponse;
import com.mgdsw.sdss.core.dto.EmployeeGrpResponse;
import com.mgdsw.sdss.core.dto.EmployeeSearchRequest;
import com.mgdsw.sdss.core.projection.JobProjection;
import com.mgdsw.sdss.core.service.EmployeeService;
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
import org.springframework.web.bind.annotation.PathVariable;
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
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
@Tag(name = "Core Employee Controller")
@RequestMapping("/api/v1/core/employee")
public class EmployeeCoreController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<SuccessResponse<EmployeeGrpResponse>> retrieveEmployeeInformation(@RequestParam Long grpNumber) {
        return ControllerResponseHelper.okResponse(employeeService.findEmployeeByGrp(grpNumber));
    }

    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Get all Employees By Filtration")
    @PageableAsQueryParam
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<EmployeeGrpResponse>> filter(EmployeeSearchRequest searchRequest,
                                                                          @PageableDefault(
                                                                                  size = Integer.MAX_VALUE,
                                                                                  sort = "id",
                                                                                  direction = Sort.Direction.DESC
                                                                          )
                                                                          @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(employeeService.filter(searchRequest, pageable));
    }

    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Send Survey Email Invitation")
    @PageableAsQueryParam
    @GetMapping("/send-survey-invitation")
    public ResponseEntity<SuccessResponse<String>> sendSurveyEmailInvitation(EmployeeSearchRequest searchRequest,
                                                                             @PageableDefault(
                                                                                     size = Integer.MAX_VALUE,
                                                                                     sort = "id",
                                                                                     direction = Sort.Direction.DESC
                                                                             )
                                                                             @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okResponse(employeeService.filterAndSendSurveyEmailInvitation(searchRequest, pageable));
    }

    @GetMapping("/organizations/{departmentId}/jobs")
    public ResponseEntity<List<JobProjection>> getJobsByOrganizationId(@PathVariable Long departmentId) {
        return ResponseEntity.ok(employeeService.getJobsByOrganizationId(departmentId));
    }

    @GetMapping("/organizations/jobs")
    public ResponseEntity<List<JobProjection>> getJobsByOrganizationIds(@RequestParam List<Long> departmentIds) {
        return ResponseEntity.ok(employeeService.getJobsByOrganizationIds(departmentIds));
    }

    @PageableAsQueryParam
    @GetMapping("/retrieve-by-jobs-and-departments")
    public ResponseEntity<PaginationResponse<EmployeeDetailsResponse>> findEmployeesByJobAndDepartment(EmployeeAudienceSearchRequest searchRequest,
                                                                                                       @PageableDefault(
                                                                                                               size = 10,
                                                                                                               sort = "fullName",
                                                                                                               direction = Sort.Direction.ASC
                                                                                                       )
                                                                                                       @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(employeeService.findEmployeesByJobAndDepartment(searchRequest, pageable));
    }
}
