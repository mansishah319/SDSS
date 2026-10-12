/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations;

import com.mgdsw.sdss.common.annotations.validator.UniqueFieldValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueFieldValidator.class)
public @interface UniqueField {
    String message() default "Duplicate value in the list";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String fieldName();
}
