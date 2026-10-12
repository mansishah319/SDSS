/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations;

import com.mgdsw.sdss.common.annotations.validator.SecureInputValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for secure input validation
 * Prevents XSS and injection attacks
 *
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jan, 2025
 */
@Documented
@Constraint(validatedBy = SecureInputValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureInput {

    String message() default "Input contains potentially dangerous content and is not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Maximum length of input
     */
    int maxLength() default 1000;

    /**
     * Whether to perform strict validation
     */
    boolean strict() default true;
}
