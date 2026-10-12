/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.OneIsPrimary;
import com.mgdsw.sdss.dto.user.expertises.UsersExpertisesRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */
public class OneIsPrimaryValidator implements ConstraintValidator<OneIsPrimary, List<UsersExpertisesRequest>> {

    OneIsPrimary oneIsPrimary;

    @Override
    public void initialize(OneIsPrimary constraintAnnotation) {
        this.oneIsPrimary = constraintAnnotation;
    }

    @Override
    public boolean isValid(List<UsersExpertisesRequest> expertisesRequests, ConstraintValidatorContext context) {
        if (expertisesRequests == null || expertisesRequests.isEmpty()) {
            return true; // No validation needed for an empty list.
        }

        // Count the number of isPrimary values set to true.
        long countTrueIsPrimary = expertisesRequests.stream()
                .filter(expertise -> Boolean.TRUE.equals(expertise.getIsPrimary()))
                .count();

        // Check if only one isPrimary is set to true.
        return countTrueIsPrimary == 1;
    }
}