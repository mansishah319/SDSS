/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.common.annotations;

import com.mgdsw.sdss.common.annotations.validator.JobListRequiredIfDepartmentListNotEmptyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 24 Feb 2025
 */
@Target({ElementType.TYPE}) // Apply at class level
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JobListRequiredIfDepartmentListNotEmptyValidator.class)
public @interface JobListRequiredIfDepartmentListNotEmpty {
    String message() default "jobList cannot be empty if departmentList is not empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
