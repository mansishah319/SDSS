/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.service;

import com.mgdsw.sdss.core.dto.DepartmentCoreResponse;
import com.mgdsw.sdss.core.dto.DepartmentEmployeeGrpResponse;
import com.mgdsw.sdss.core.dto.SubDepartmentCoreResponse;
import com.mgdsw.sdss.core.entity.Department;
import com.mgdsw.sdss.core.projection.DepartmentHierarchyProjection;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */
public interface DepartmentService {

    DepartmentEmployeeGrpResponse findEmployeeFullDepartmentsDetailsByGrp(Long grp);

    List<DepartmentCoreResponse> getAllDepartments();

    List<SubDepartmentCoreResponse> getAllSubDepartmentsByDepartmentId(Long departmentId);

    Department findById(Long departmentId);

    List<DepartmentCoreResponse> retrieveAllSectors();

    List<SubDepartmentCoreResponse> findAllSectionBySubDepartmentId(Long subDepartmentId);

    List<DepartmentCoreResponse> retrieveDepartmentByEmployeeGrpNumber(Long grpNumber);

    boolean isUserGrpNumberHasThisDepartment(Long grpNumber, String departmentName);

    List<SubDepartmentCoreResponse> getAllSubDepartmentsByDepartmentIdList(List<Long> departmentIdList);

    DepartmentHierarchyProjection getDepartmentHierarchy(Long depId);

    List<DepartmentCoreResponse> getAllDepartmentsWithoutRestriction();
}
