/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.service;

import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.core.dto.EmployeeAudienceSearchRequest;
import com.mgdsw.sdss.core.dto.EmployeeDetailsResponse;
import com.mgdsw.sdss.core.dto.EmployeeDropDownResponse;
import com.mgdsw.sdss.core.dto.EmployeeGrpResponse;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.core.dto.EmployeeSearchRequest;
import com.mgdsw.sdss.core.dto.SdssEmployeeResponse;
import com.mgdsw.sdss.core.entity.Employee;
import com.mgdsw.sdss.core.projection.JobProjection;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
public interface EmployeeService {

    SdssEmployeeResponse retrieveEmployeeWithDepartmentInfoByGrpNumber(Long grpNumber);

    List<EmployeeGrpResponse> employeeGrpFiltration(String filteredGrpNumber);

    EmployeeGrpResponse findEmployeeByGrp(Long grpNumber);

    List<EmployeeDropDownResponse> retrieveAllDropDownListOfEmployees();

    EmployeeGrpResponse findEmployeeByGrpInternally(Long grpNumber);

    Employee findEmployeeByIdOrNull(Long grpNumber);

    PaginationResponse<EmployeeGrpResponse> filter(EmployeeSearchRequest searchRequest, Pageable pageable);

    String filterAndSendSurveyEmailInvitation(EmployeeSearchRequest searchRequest, Pageable pageable);

    List<Long> retrieveEmployeesGrbNumbersByJobsAndDepartmentList(List<String> jobList, List<String> departmentList);

    List<EmployeeInfoKeyResponse> retrieveAllEmployeesIn(List<Long> grpNumberList);

    EmployeeInfoKeyResponse retrieveInfoForEmployeeById(Long grpNumber);

    Employee findByIdOrThrowException(Long employeeId);

    List<Employee> findAllByIdsIn(List<Long> grpNumberList);

    List<JobProjection> getJobsByOrganizationId(Long organizationId);

    List<JobProjection> getJobsByOrganizationIds(List<Long> organizationIds);

    PaginationResponse<EmployeeDetailsResponse> findEmployeesByJobAndDepartment(EmployeeAudienceSearchRequest searchRequest, Pageable pageable);
}
