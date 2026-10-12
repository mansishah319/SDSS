/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator.urls;

import com.mgdsw.sdss.common.annotations.ValidURL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Dec, 2023
 */

public class URLValidator implements ConstraintValidator<ValidURL, String> {
    @Override
    public void initialize(ValidURL constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            // Null or empty URL is considered valid
            return true;
        }

        try {
            new URL(value);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
