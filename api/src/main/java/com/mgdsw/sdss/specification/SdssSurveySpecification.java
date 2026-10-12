/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.specification;

import com.mgdsw.sdss.common.base.BaseEntity_;
import com.mgdsw.sdss.common.base.BaseSearchSpecification;
import com.mgdsw.sdss.common.base.lookups.EntityLookupInfo_;
import com.mgdsw.sdss.dto.surveys.SdssSurveySearch;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurvey_;
import com.mgdsw.sdss.enums.SurveyStatus;

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
public class SdssSurveySpecification extends BaseSearchSpecification<SdssSurveySearch, SdssSurvey> {

    public SdssSurveySpecification(SdssSurveySearch search) {
        super(search);
    }

    @Override
    public Predicate toPredicate(Root<SdssSurvey> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, SdssSurveySearch search) {
        List<Predicate> predicateList = new ArrayList<>();

        if (Objects.nonNull(search.getId())) {
            predicateList.add(
                    super.applyLike(BaseEntity_.ID, search.getId().toString())
                            .toPredicate(root, query, criteriaBuilder)
            );
        }

        if (Objects.nonNull(search.getStatus())) {
            predicateList.add(
                    super.applyLike(SdssSurvey_.STATUS, SurveyStatus.fromString(search.getStatus()).toString())
                            .toPredicate(root, query, criteriaBuilder)
            );
        }

        if (Objects.nonNull(search.getActive())) {
            predicateList.add(
                    super.applyLike(SdssSurvey_.ACTIVE, search.getActive().toString())
                            .toPredicate(root, query, criteriaBuilder)
            );
        }

        if (Objects.nonNull(search.getNumberOfRounds())) {
            predicateList.add(
                    super.applyLike(SdssSurvey_.NUMBER_OF_ROUNDS, search.getNumberOfRounds().toString())
                            .toPredicate(root, query, criteriaBuilder)
            );
        }

        if (Objects.nonNull(search.getManualHandle())) {
            predicateList.add(
                    super.applyLike(SdssSurvey_.MANUAL_HANDLE, search.getManualHandle().toString())
                            .toPredicate(root, query, criteriaBuilder)
            );
        }

        if (Objects.nonNull(search.getTitle())) {
            predicateList.add(
                    super.applyOr(EntityLookupInfo_.TITLE_AR, EntityLookupInfo_.TITLE_EN, search.getTitle())
                            .toPredicate(root, query, criteriaBuilder)
            );
        }

        if (Objects.nonNull(search.getDescription())) {
            predicateList.add(
                    super.applyOr(EntityLookupInfo_.DESCRIPTION_AR, EntityLookupInfo_.DESCRIPTION_EN, search.getDescription())
                            .toPredicate(root, query, criteriaBuilder)
            );
        }

        query.distinct(true);

        return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
    }
}
