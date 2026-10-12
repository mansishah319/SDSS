/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.specification;

import com.mgdsw.sdss.common.base.BaseSearchSpecification;
import com.mgdsw.sdss.dto.user.UserDropdownSearch;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.User_;
import com.mgdsw.sdss.entity.user.UsersExpertises;
import com.mgdsw.sdss.entity.variety.Expertises;
import com.mgdsw.sdss.enums.ExpertLevel;
import com.mgdsw.sdss.enums.Status;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDropdownSpecification extends BaseSearchSpecification<UserDropdownSearch, User> {

    public UserDropdownSpecification(UserDropdownSearch search) {
        super(search);
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb, UserDropdownSearch search) {
        List<Predicate> predicates = new ArrayList<>();

        // Base status filter (ACTIVE users)
        predicates.add(cb.equal(root.get("status"), Status.ACTIVE));

        // Search term filter (name or email)
        if (StringUtils.isNotBlank(search.getSearchTerm())) {
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get(User_.FULL_NAME)), "%" + search.getSearchTerm().toLowerCase() + "%"),
                            cb.like(cb.lower(root.get(User_.EMAIL)), "%" + search.getSearchTerm().toLowerCase() + "%")
                    )
            );
        }

        // Expertise filtering
        if (StringUtils.isNotBlank(search.getExpertLevel()) ||
                StringUtils.isNotBlank(search.getAreaOfExpertise()) ||
                search.getIsPrimaryExpertise() != null) {

            Join<User, UsersExpertises> expertisesJoin = root.join("expertisesList", JoinType.LEFT);

            if (search.getIsPrimaryExpertise() != null) {
                predicates.add(cb.equal(expertisesJoin.get("isPrimary"), search.getIsPrimaryExpertise()));
            }

            if (StringUtils.isNotBlank(search.getExpertLevel())) {
                predicates.add(cb.equal(expertisesJoin.get("expertLevel"),
                        ExpertLevel.valueOf(search.getExpertLevel())));
            }

            if (StringUtils.isNotBlank(search.getAreaOfExpertise())) {
                Join<UsersExpertises, Expertises> expertiseJoin = expertisesJoin.join("expertises", JoinType.LEFT);
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(expertiseJoin.get("nameEn")), "%" + search.getAreaOfExpertise().toLowerCase() + "%"),
                                cb.like(cb.lower(expertiseJoin.get("nameAr")), "%" + search.getAreaOfExpertise().toLowerCase() + "%")
                        )
                );
            }
        }

        if (Objects.nonNull(search.getDepartment()) && StringUtils.isNotBlank(search.getDepartment()) && !search.getDepartment().isEmpty()) {
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get(User_.DEPARTMENT_NAME)), "%" + search.getDepartment() + "%"),
                            cb.like(cb.lower(root.get(User_.DEPARTMENT_ID)), "%" + search.getDepartment() + "%")
                    )
            );
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
