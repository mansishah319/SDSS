/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.base;

import com.mgdsw.sdss.common.constants.DpConstants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.PERCENTAGE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseSearchSpecification<S, T> implements Specification<T> {

    private static final long serialVersionUID = 1L;

    private transient S search;

    public abstract Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, S search);

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        return this.toPredicate(root, query, criteriaBuilder, this.search);
    }

    protected Specification<T> getTimeBetween(LocalDate startCreatedAtDate, LocalDate endCreatedAtDate, String entityDateName) {
        LocalTime timePart = LocalTime.parse(DpConstants.DateFormat.ZEROS_OF_HOUR_MIN_SEC);
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.between(
                        root.get(entityDateName),
                        LocalDateTime.of(startCreatedAtDate, timePart),
                        LocalDateTime.of(endCreatedAtDate, timePart)
                );
    }

    protected Specification<T> getTimeBetween(LocalDateTime startCreatedAtDate, LocalDateTime endCreatedAtDate, String entityDateName) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.between(
                        root.get(entityDateName),
                        startCreatedAtDate,
                        endCreatedAtDate
                );
    }

    protected Specification<T> applyOr(String firstName, String secondName, String value) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(root.get(firstName).as(String.class), PERCENTAGE + value + PERCENTAGE),
                        criteriaBuilder.like(root.get(secondName).as(String.class), PERCENTAGE + value + PERCENTAGE)
                );
    }

    protected Specification<T> applyLike(String firstName, String firstValue) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(root.get(firstName).as(String.class), PERCENTAGE + firstValue + PERCENTAGE);
    }
}
