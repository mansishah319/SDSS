/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.repository;

import com.mgdsw.sdss.core.entity.Employee;
import com.mgdsw.sdss.core.projection.JobProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    @Query("SELECT e FROM Employee e WHERE CAST(e.id AS string) LIKE %:employeeId%")
    List<Employee> findByIdContaining(String employeeId);

    List<Employee> findByIdIn(List<Long> idList);

    @Query("SELECT DISTINCT " +
            "e.jobId AS jobId, " +
            "e.jobName AS jobName " +
            "FROM Employee e " +
            "WHERE e.departmentId = :organizationId " +
            "AND e.jobId IS NOT NULL " +
            "ORDER BY e.jobName")
    List<JobProjection> findDistinctJobsByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT DISTINCT " +
            "e.jobId AS jobId, " +
            "e.jobName AS jobName " +
            "FROM Employee e " +
            "WHERE e.departmentId IN (:organizationIds) " +
            "AND e.jobId IS NOT NULL " +
            "ORDER BY e.jobName")
    List<JobProjection> findDistinctJobsByOrganizationIds(@Param("organizationIds") List<Long> organizationIds);
}
