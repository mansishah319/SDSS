/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.service.impl;

import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.core.dto.EmployeeAudienceSearchRequest;
import com.mgdsw.sdss.core.dto.EmployeeDetailsResponse;
import com.mgdsw.sdss.core.dto.EmployeeDropDownResponse;
import com.mgdsw.sdss.core.dto.EmployeeGrpResponse;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.core.dto.EmployeeSearchRequest;
import com.mgdsw.sdss.core.dto.SdssEmployeeResponse;
import com.mgdsw.sdss.core.entity.Employee;
import com.mgdsw.sdss.core.entity.StfEmployeeInfo;
import com.mgdsw.sdss.core.projection.JobProjection;
import com.mgdsw.sdss.core.repository.EmployeeRepository;
import com.mgdsw.sdss.core.service.DepartmentService;
import com.mgdsw.sdss.core.service.EmployeeService;
import com.mgdsw.sdss.core.service.StfEmployeeInfoService;
import com.mgdsw.sdss.core.specification.EmployeeAudienceSpecification;
import com.mgdsw.sdss.core.specification.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StfEmployeeInfoService stfEmployeeInfoService;
    private final DepartmentService departmentService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public SdssEmployeeResponse retrieveEmployeeWithDepartmentInfoByGrpNumber(Long grpNumber) {
        Employee employee = findEmployeeByIdWithoutException(grpNumber);
        if (Objects.isNull(employee)) {
            return null;
        }

        SdssEmployeeResponse response = modelMapper.map(
                departmentService.findEmployeeFullDepartmentsDetailsByGrp(grpNumber), SdssEmployeeResponse.class);
        modelMapper.map(employee, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeGrpResponse> employeeGrpFiltration(String filteredGrpNumber) {
        return employeeRepository.findByIdContaining(filteredGrpNumber).stream()
                .map(e -> modelMapper.map(e, EmployeeGrpResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeGrpResponse findEmployeeByGrp(Long grpNumber) {
        EmployeeGrpResponse employeeGrpResponse = modelMapper.map(this.findById(grpNumber), EmployeeGrpResponse.class);
        StfEmployeeInfo stfEmployeeInfo = stfEmployeeInfoService.findByGrpNumberOrNull(grpNumber);
        if (Objects.nonNull(stfEmployeeInfo)) {
            log.info("stfEmployee ID is -> {}", stfEmployeeInfo.getEmployeeId());
            employeeGrpResponse.setEmpMobile(stfEmployeeInfo.getEmpMobile());
        }
        return employeeGrpResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeGrpResponse findEmployeeByGrpInternally(Long grpNumber) {
        Employee employeeByIdWithoutException = findEmployeeByIdWithoutException(grpNumber);
        return Objects.nonNull(employeeByIdWithoutException) ? modelMapper.map(employeeByIdWithoutException, EmployeeGrpResponse.class) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDropDownResponse> retrieveAllDropDownListOfEmployees() {
        return employeeRepository.findAll().stream().map(employee ->
                modelMapper.map(employee, EmployeeDropDownResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findEmployeeByIdOrNull(Long grpNumber) {
        return findEmployeeByIdWithoutException(grpNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<EmployeeGrpResponse> filter(EmployeeSearchRequest searchRequest, Pageable pageable) {
        return retrieveFiltrationResult(searchRequest, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public String filterAndSendSurveyEmailInvitation(EmployeeSearchRequest searchRequest, Pageable pageable) {
        Page<Employee> employeePage = retrieveEmployeePage(searchRequest, pageable);
        log.info("employeePage total elements is : {}", employeePage.getTotalElements());
        return "Email sent Successfully.";
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> retrieveEmployeesGrbNumbersByJobsAndDepartmentList(List<String> jobList, List<String> departmentList) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        return this.retrieveEmployeePage(
                EmployeeSearchRequest.builder()
                        .departmentList(departmentList)
                        .jobList(jobList)
                        .build(), pageable).getContent().stream().map(Employee::getId).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeInfoKeyResponse> retrieveAllEmployeesIn(List<Long> grpNumberList) {
        return employeeRepository.findByIdIn(grpNumberList)
                .stream().map(employee -> modelMapper.map(employee, EmployeeInfoKeyResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeInfoKeyResponse retrieveInfoForEmployeeById(Long grpNumber) {
        return modelMapper.map(this.findById(grpNumber), EmployeeInfoKeyResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findByIdOrThrowException(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomNotFoundException(
                        String.format(LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), employeeId),
                        LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAllByIdsIn(List<Long> grpNumberList) {
        return employeeRepository.findByIdIn(grpNumberList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobProjection> getJobsByOrganizationId(Long organizationId) {
        return employeeRepository.findDistinctJobsByOrganizationId(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobProjection> getJobsByOrganizationIds(List<Long> organizationIds) {
        return employeeRepository.findDistinctJobsByOrganizationIds(organizationIds);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<EmployeeDetailsResponse> findEmployeesByJobAndDepartment(EmployeeAudienceSearchRequest searchRequest, Pageable pageable) {
        Page<Employee> employeePage = employeeRepository.findAll(new EmployeeAudienceSpecification(searchRequest), pageable);
        List<EmployeeDetailsResponse> employees = employeePage.getContent().stream()
                .map(employee -> modelMapper.map(employee, EmployeeDetailsResponse.class))
                .collect(Collectors.toList());
        return PaginationResponse.initPaginationResponse(employeePage, pageable, employees);
    }

    private PaginationResponse<EmployeeGrpResponse> retrieveFiltrationResult(EmployeeSearchRequest searchRequest, Pageable pageable) {
        Page<Employee> employeePage = this.retrieveEmployeePage(searchRequest, pageable);
        List<EmployeeGrpResponse> employeeGrpResponses =
                employeePage.getContent().stream().map(employee -> modelMapper.map(employee, EmployeeGrpResponse.class)).collect(Collectors.toList());
        return PaginationResponse.initPaginationResponse(employeePage, pageable, employeeGrpResponses);
    }

    private Page<Employee> retrieveEmployeePage(EmployeeSearchRequest searchRequest, Pageable pageable) {
        return employeeRepository.findAll(new EmployeeSpecification(searchRequest), pageable);
    }

    private Employee findById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomNotFoundException(
                        String.format(LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), employeeId),
                        LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode()));
    }

    private Employee findEmployeeByIdWithoutException(Long grpNumber) {
        return employeeRepository.findById(grpNumber).orElse(null);
    }
}

