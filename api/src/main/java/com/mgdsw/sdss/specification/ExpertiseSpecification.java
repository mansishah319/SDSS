/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.specification;

import com.mgdsw.sdss.common.base.BaseSearchSpecification;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.dto.expertises.ExpertiseFilterRequest;
import com.mgdsw.sdss.entity.variety.Expertises;
import com.mgdsw.sdss.entity.variety.Expertises_;
import lombok.NonNull;
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
 * @createdOn Mar, 2024
 */
public class ExpertiseSpecification extends BaseSearchSpecification<ExpertiseFilterRequest, Expertises> {

    public ExpertiseSpecification(ExpertiseFilterRequest search) {
        super(search);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Expertises> root,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder,
                                 ExpertiseFilterRequest search) {

        List<Predicate> predicateList = new ArrayList<>();

        if (Objects.nonNull(search.getExpertiseName()) && StringUtils.isNotEmpty(search.getExpertiseName()))
            predicateList.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get(Expertises_.NAME_EN).as(String.class),
                                    DpConstants.Characters.PERCENTAGE + search.getExpertiseName() + DpConstants.Characters.PERCENTAGE),
                            criteriaBuilder.like(root.get(Expertises_.NAME_AR).as(String.class),
                                    DpConstants.Characters.PERCENTAGE + search.getExpertiseName() + DpConstants.Characters.PERCENTAGE)
                    )
            );
        return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
    }
}
