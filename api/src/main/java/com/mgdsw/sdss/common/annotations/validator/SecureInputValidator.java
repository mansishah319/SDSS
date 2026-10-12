/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.SecureInput;
import com.mgdsw.sdss.common.security.InputValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for SecureInput annotation
 *
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jan, 2025
 */
@Slf4j
@RequiredArgsConstructor
public class SecureInputValidator implements ConstraintValidator<SecureInput, String> {

    private final InputValidationUtil inputValidationUtil;

    private int maxLength;
    private boolean strict;

    @Override
    public void initialize(SecureInput constraintAnnotation) {
        // Note: allowHtmlEntities is not used in the current implementation
        // but kept in annotation for future extensibility
        this.maxLength = constraintAnnotation.maxLength();
        this.strict = constraintAnnotation.strict();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // null or empty values are considered valid
        }

        // Check length
        if (value.length() > maxLength) {
            log.warn("Input exceeds maximum length: {} > {}", value.length(), maxLength);
            return false;
        }

        // Basic XSS and injection validation
        if (!inputValidationUtil.isValidInput(value)) {
            log.warn("Dangerous content detected in input: {}",
                    value.length() > 50 ? value.substring(0, 50) + "..." : value);
            return false;
        }

        // Strict validation for additional security
        if (strict && inputValidationUtil.containsSqlInjection(value)) {
            log.warn("SQL injection pattern detected in input");
            return false;
        }


        return true;
    }
}
