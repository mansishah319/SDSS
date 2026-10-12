/**
 * ***************************************************************
 * Copyright ©1956-2026 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.specification;

import com.mgdsw.sdss.common.base.BaseSearchSpecification;
import com.mgdsw.sdss.core.dto.EmployeeAudienceSearchRequest;
import com.mgdsw.sdss.core.entity.Employee;
import com.mgdsw.sdss.core.entity.Employee_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class EmployeeAudienceSpecification extends BaseSearchSpecification<EmployeeAudienceSearchRequest, Employee> {

    public EmployeeAudienceSpecification(EmployeeAudienceSearchRequest search) {
        super(search);
    }

    @Override
    public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, EmployeeAudienceSearchRequest search) {
        List<Predicate> predicates = new ArrayList<>();

        if (search == null) {
            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        }

        if (!ObjectUtils.isEmpty(search.getDepartmentIds())) {
            predicates.add(root.get(Employee_.DEPARTMENT_ID).in(search.getDepartmentIds()));
        }

        if (!ObjectUtils.isEmpty(search.getJobIds())) {
            predicates.add(root.get(Employee_.JOB_ID).in(search.getJobIds()));
        }

        if (StringUtils.isNotBlank(search.getSearch())) {
            String searchValue = search.getSearch().trim();
            predicates.add(criteriaBuilder.or(
                    super.applyLike(Employee_.ID, searchValue).toPredicate(root, query, criteriaBuilder),
                    super.applyLike(Employee_.EMPLOYEE_NUMBER, searchValue).toPredicate(root, query, criteriaBuilder),
                    super.applyLike(Employee_.FULL_NAME, searchValue).toPredicate(root, query, criteriaBuilder),
                    super.applyLike(Employee_.SLM_NAME, searchValue).toPredicate(root, query, criteriaBuilder),
                    super.applyLike(Employee_.DEPARTMENT_NAME, searchValue).toPredicate(root, query, criteriaBuilder),
                    super.applyLike(Employee_.JOB_NAME, searchValue).toPredicate(root, query, criteriaBuilder),
                    super.applyLike(Employee_.EMAIL_ADDRESS, searchValue).toPredicate(root, query, criteriaBuilder)
            ));
        }

        query.distinct(true);

        return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
    }
}
