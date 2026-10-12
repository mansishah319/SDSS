/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.specification;

import com.mgdsw.sdss.common.base.BaseSearchSpecification;
import com.mgdsw.sdss.core.dto.EmployeeSearchRequest;
import com.mgdsw.sdss.core.entity.Employee;
import com.mgdsw.sdss.core.entity.Employee_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */
public class EmployeeSpecification extends BaseSearchSpecification<EmployeeSearchRequest, Employee> {

    public EmployeeSpecification(EmployeeSearchRequest search) {
        super(search);
    }

    @Override
    public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, EmployeeSearchRequest search) {

        List<Predicate> predicateList = new ArrayList<>();

        if (Objects.nonNull(search.getId())) {
            predicateList.add(
                    super.applyLike(Employee_.ID, search.getId().toString())
                            .toPredicate(root, query, criteriaBuilder)
            );
        }

        // Department filter (for a list of departments)
        if (Objects.nonNull(search.getDepartmentList()) && !search.getDepartmentList().isEmpty()) {
            predicateList.add(
                    criteriaBuilder.or(
                            search.getDepartmentList().stream()
                                    .map(department ->
                                            super.applyOr(Employee_.DEPARTMENT_ID, Employee_.DEPARTMENT_NAME, department)
                                                    .toPredicate(root, query, criteriaBuilder)
                                    )
                                    .toArray(Predicate[]::new)
                    )
            );
        }

        // Job filter (for a list of jobs)
        if (Objects.nonNull(search.getJobList()) && !search.getJobList().isEmpty()) {
            predicateList.add(
                    criteriaBuilder.or(
                            search.getJobList().stream()
                                    .map(job ->
                                            super.applyOr(Employee_.JOB_ID, Employee_.JOB_NAME, job)
                                                    .toPredicate(root, query, criteriaBuilder)
                                    )
                                    .toArray(Predicate[]::new)
                    )
            );
        }

        query.distinct(true);

        return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
    }
}
