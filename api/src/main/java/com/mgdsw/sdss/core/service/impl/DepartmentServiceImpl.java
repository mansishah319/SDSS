/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.service.impl;

import com.mgdsw.sdss.common.config.security.dto.UserPrincipal;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.core.dto.DepartmentCoreResponse;
import com.mgdsw.sdss.core.dto.DepartmentEmployeeGrpResponse;
import com.mgdsw.sdss.core.dto.SubDepartmentCoreResponse;
import com.mgdsw.sdss.core.entity.Department;
import com.mgdsw.sdss.core.projection.DepartmentHierarchyProjection;
import com.mgdsw.sdss.core.repository.DepartmentRepository;
import com.mgdsw.sdss.core.service.DepartmentService;
import com.mgdsw.sdss.enums.SystemRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "department", key = "#grp")
    public DepartmentEmployeeGrpResponse findEmployeeFullDepartmentsDetailsByGrp(Long grp) {
        Department sectorByEmployee = departmentRepository.getMainSectorDepartmentByEmployee(grp);
        return DepartmentEmployeeGrpResponse.builder()
                .departments(departmentRepository.departmentByEmployee(grp).stream()
                        .map(department -> modelMapper.map(department, DepartmentCoreResponse.class))
                        .collect(Collectors.toList()))
                .mainSelectSectorsByEmployee(departmentRepository.mainSelectSectorByEmployee(grp).stream()
                        .map(subDep -> modelMapper.map(subDep, DepartmentCoreResponse.class))
                        .collect(Collectors.toList()))
                .sector(modelMapper.map(Objects.nonNull(sectorByEmployee) ? sectorByEmployee : new DepartmentCoreResponse(), DepartmentCoreResponse.class))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentCoreResponse> getAllDepartments() {
        UserPrincipal userPayload = DpCommonUtil.getUserPayload();
        Set<String> authorities = DpCommonUtil.retrieveCurrentUserAuthorities();
        Long grpNumber = userPayload.getGrpNumber();
        if (authorities.contains(SystemRoles.COMMANDER.getKey()) || authorities.contains(SystemRoles.ADMIN.getKey())) {
            log.info("The user with grpNumber : {} --> is a [MODERATOR & ADMIN]", grpNumber);
            return departmentRepository.findAllMainDepartments().stream()
                    .map(department -> modelMapper.map(department, DepartmentCoreResponse.class))
                    .collect(Collectors.toList());
        } else if (authorities.contains(SystemRoles.MODERATOR.getKey())) {
            log.info("The user with grpNumber : {} --> is a [MODERATOR] Only.", grpNumber);
            return this.departmentByEmployee(grpNumber).stream()
                    .map(department -> modelMapper.map(department, DepartmentCoreResponse.class))
                    .collect(Collectors.toList());
        } else {
            throw new CustomAuthorizationException(LocalizedErrorCodes.SDSS_UNAUTHORIZED_USER_FOR_DECISION.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), LocalizedErrorCodes.SDSS_UNAUTHORIZED_USER_FOR_DECISION.getErrorCode());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentCoreResponse> getAllDepartmentsWithoutRestriction() {
        return departmentRepository.findAllMainDepartments().stream()
                .map(department -> modelMapper.map(department, DepartmentCoreResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentCoreResponse> retrieveDepartmentByEmployeeGrpNumber(Long grpNumber) {
        return this.departmentByEmployee(grpNumber).stream()
                .map(department -> modelMapper.map(department, DepartmentCoreResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubDepartmentCoreResponse> getAllSubDepartmentsByDepartmentId(Long departmentId) {
        return this.getAllSubDepartmentsByDepartmentIdInternal(departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubDepartmentCoreResponse> findAllSectionBySubDepartmentId(Long subDepartmentId) {
        try {
            Department existSubDepartment = this.retrieveById(subDepartmentId);
            log.info("The SubDepartment ID is ->> {}", existSubDepartment.getId());
            return departmentRepository.findAllSectionBySubDepartmentId(existSubDepartment.getId(), existSubDepartment.getDepartmentTypeCode()).stream()
                    .map(subDepartment -> modelMapper.map(subDepartment, SubDepartmentCoreResponse.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Department findById(Long departmentId) {
        return this.retrieveById(departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentCoreResponse> retrieveAllSectors() {
        return departmentRepository.retrieveAllSectors().stream()
                .map(sector -> modelMapper.map(sector, DepartmentCoreResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserGrpNumberHasThisDepartment(Long grpNumber, String departmentName) {
        return departmentRepository.departmentByEmployee(grpNumber).stream()
                .anyMatch(department -> department.getDepartmentName().contains(departmentName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubDepartmentCoreResponse> getAllSubDepartmentsByDepartmentIdList(List<Long> departmentIdList) {
        try {
            return departmentIdList.stream()
                    .map(this::getAllSubDepartmentsByDepartmentIdInternal)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error while fetching sub-departments by department IDs: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentHierarchyProjection getDepartmentHierarchy(Long depId) {
        return departmentRepository.findDepartmentHierarchy(depId);
    }

    private List<SubDepartmentCoreResponse> getAllSubDepartmentsByDepartmentIdInternal(Long departmentId) {
        try {
            Department existDepartment = this.retrieveById(departmentId);
            log.info("The Department ID is ->> {}", existDepartment.getId());
            return departmentRepository.findAllSubDepartment(existDepartment.getId(), existDepartment.getDepartmentTypeCode()).stream()
                    .map(department -> modelMapper.map(department, SubDepartmentCoreResponse.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private Department retrieveById(Long departmentId) {
        return departmentRepository.findById(departmentId).orElseThrow(() -> new CustomNotFoundException(String.format(LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), departmentId), LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode()));
    }

    private List<Department> departmentByEmployee(Long grpNumber) {
        return departmentRepository.departmentByEmployee(grpNumber);
    }
}
