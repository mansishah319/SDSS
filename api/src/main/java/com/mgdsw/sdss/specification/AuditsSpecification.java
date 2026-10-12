/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.specification;

import com.mgdsw.sdss.common.base.BaseEntity_;
import com.mgdsw.sdss.common.base.BaseSearchSpecification;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.DpDateUtil;
import com.mgdsw.sdss.dto.audits.AuditsSearchRequest;
import com.mgdsw.sdss.entity.variety.Audits;
import com.mgdsw.sdss.entity.variety.Audits_;
import org.apache.commons.lang3.StringUtils;

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
 * @createdOn Sep, 2023
 */

public class AuditsSpecification extends BaseSearchSpecification<AuditsSearchRequest, Audits> {

    public AuditsSpecification(AuditsSearchRequest search) {
        super(search);
    }

    @Override
    public Predicate toPredicate(Root<Audits> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, AuditsSearchRequest search) {

        List<Predicate> predicateList = new ArrayList<>();

        if (Objects.nonNull(search.getEventEn()) && StringUtils.isNotEmpty(search.getEventEn()))
            predicateList.add(criteriaBuilder.like(root.get(Audits_.EVENT_EN).as(String.class),
                    DpConstants.Characters.PERCENTAGE + search.getEventEn() + DpConstants.Characters.PERCENTAGE));

        if (Objects.nonNull(search.getEventAr()) && StringUtils.isNotEmpty(search.getEventAr()))
            predicateList.add(criteriaBuilder.like(root.get(Audits_.EVENT_AR).as(String.class),
                    DpConstants.Characters.PERCENTAGE + search.getEventAr() + DpConstants.Characters.PERCENTAGE));

        if (Objects.nonNull(search.getFromDate()) && StringUtils.isNotEmpty(search.getFromDate())
                && Objects.nonNull(search.getToDate()) && StringUtils.isNotEmpty(search.getToDate()))
            predicateList.add(
                    super.getTimeBetween(
                            DpDateUtil.toLocalDate(search.getFromDate()),
                            DpDateUtil.toLocalDate(search.getToDate()),
                            BaseEntity_.CREATED_AT
                    ).toPredicate(root, query, criteriaBuilder));

        return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
    }
}
