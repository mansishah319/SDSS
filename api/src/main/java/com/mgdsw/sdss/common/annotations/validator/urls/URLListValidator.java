/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator.urls;

import com.mgdsw.sdss.common.annotations.ValidURLList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Dec, 2023
 */
public class URLListValidator implements ConstraintValidator<ValidURLList, List<String>> {
    @Override
    public void initialize(ValidURLList constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            // Null or empty list is considered valid
            return true;
        }

        List<Boolean> booleanList = value.stream().map(urlStr -> {
            if (urlStr == null || urlStr.isEmpty()) {
                return true;
            }

            try {
                new URL(urlStr);
                return true;
            } catch (MalformedURLException e) {
                return false;
            }
        }).collect(Collectors.toList());

        return !booleanList.contains(false);
    }
}
