/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.repository;

import com.mgdsw.sdss.core.entity.Department;
import com.mgdsw.sdss.core.projection.DepartmentHierarchyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query(nativeQuery = true, value =
            "SELECT deps.* " +
                    "FROM dp_hr_departments_grp deps " +
                    "WHERE deps.SUB_ORGANIZATION_TYPE_CODE = 70 " +
                    "CONNECT BY PRIOR deps.PARENT_ORGANIZATION_ID = deps.SUB_ORGANIZATION_ID " +
                    "START WITH deps.SUB_ORGANIZATION_ID IN ( " +
                    "    SELECT emp.PARENT_ORGANIZATION_ID " +
                    "    FROM dp_emp_mv_grp emp " +
                    "    WHERE emp.GRP_SERL_NO = :grpNumber AND :grpNumber IS NOT NULL)"
    )
    Department getMainSectorDepartmentByEmployee(@Param("grpNumber") Long grpNumber);

    @Query(nativeQuery = true, value = "select * from dp_hr_departments_grp dep " +
            "where " +
            "DEP.SUB_ORGANIZATION_ID in ( " +
            "select decode(nvl(EMP.SECTION_ID,0),0, decode(EMP.JOB_ID,2465,EMP.ORGANIZATION_ID,2466,EMP.ORGANIZATION_ID, " +
            "EMP.PARENT_ORGANIZATION_ID),EMP.SECTION_ID) from dp_emp_mv_grp emp where EMP.GRP_SERL_NO = :grpNumber )")
    List<Department> departmentByEmployee(@Param("grpNumber") Long grpNumber);

    @Query(nativeQuery = true, value = "select * from dp_hr_departments_grp dep " +
            "where " +
            "DEP.SUB_ORGANIZATION_TYPE_CODE in (1,22,16) " +
            "and " +
            "DEP.PARENT_ORGANIZATION_TYPE_CODE in (70,90) " +
            "connect by prior DEP.SUB_ORGANIZATION_ID = DEP.PARENT_ORGANIZATION_ID " +
            "start with " +
            " DEP.SUB_ORGANIZATION_ID in ( " +
            " select DEPS.SUB_ORGANIZATION_ID from dp_hr_departments_grp deps " +
            " where DEPS.SUB_ORGANIZATION_TYPE_CODE = 70  " +
            "    connect by prior DEPS.PARENT_ORGANIZATION_ID = DEPS.SUB_ORGANIZATION_ID " +
            "    start with DEPS.SUB_ORGANIZATION_ID in ( " +
            "            select EMP.PARENT_ORGANIZATION_ID from dp_emp_mv_grp emp where EMP.GRP_SERL_NO = :grpNumber ))")
    List<Department> mainSelectSectorByEmployee(@Param("grpNumber") Long grpNumber);  // All Sectors by User Grp Number

    @Query(value = "select * from DP_HR_DEPARTMENTS_GRP dep "
            + "where "
            + "SUB_ORGANIZATION_TYPE_CODE =decode(:departmentType,22,3,40) "
            + "connect by prior dep.SUB_ORGANIZATION_ID = dep.PARENT_ORGANIZATION_ID "
            + "start with SUB_ORGANIZATION_ID = :departmentId", nativeQuery = true)
    List<Department> findAllSubDepartment(@Param("departmentId") Long departmentId, @Param("departmentType") Long departmentType);

    @Query(value = "select dept from Department dept where dept.departmentTypeCode = 70"
            + " order by dept.depOrder")
    List<Department> retrieveAllSectors();

    @Query(value = "select dept from Department dept where dept.departmentTypeCode in (1,22)")
    List<Department> findAllGeneralDeptAndPoliceStation();

    @Query(value = "select dept from Department dept where dept.departmentTypeCode in (1,22,70,50,500)"
            + " and dept.parentDepartmentTypeCode in (70,90,100,50) order by dept.depOrder")
    List<Department> findAllMainDepartments();

    @Query(value = "select * from DP_HR_DEPARTMENTS_OPR dep "
            + "where "
            + "SUB_ORGANIZATION_TYPE_CODE =decode(:subDepartmentType,3,4,3) "
            + "connect by prior dep.SUB_ORGANIZATION_ID = dep.PARENT_ORGANIZATION_ID "
            + "start with SUB_ORGANIZATION_ID = :subDepartmentId", nativeQuery = true)
    List<Department> findAllSectionBySubDepartmentId(@Param("subDepartmentId") Long subDepartmentId, @Param("subDepartmentType") Long subDepartmentType);

    @Query(
            value =
                    "SELECT " +
                            "    d.SUB_ORGANIZATION_ID AS sectorId, " +
                            "    d.SUBORDINATE_ORGANIZATION AS sectorName, " +
                            "    d.SUB_ORGANIZATION_TYPE_CODE AS sectorTypeCode, " +
                            "    d.PARENT_ORGANIZATION_ID AS parentDepartmentId " +
                            "FROM DP_HR_DEPARTMENTS_GRP d " +
                            "WHERE d.SUB_ORGANIZATION_ID = EVAL_GRP_UTIL.GET_SECTOR_DEP(:depId)",
            nativeQuery = true
    )
    DepartmentHierarchyProjection findDepartmentHierarchy(@Param("depId") Long depId);
}
