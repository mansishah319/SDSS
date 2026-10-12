package com.mgdsw.sdss.common.annotations;

import com.mgdsw.sdss.common.annotations.validator.EnArCharactersOnlyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.EN_AR_NOT_CONTAIN_SPECIAL_CHARS;

/**
 * -----------------------------------------------------------------------------
 * Project: sdss
 * Author: engmoabdu
 * Created On: 08/10/2025 at 6:07 AM
 * Email: eng.mo.abdu@gmail.com
 *
 * @version: vr0.1
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Dubai Police
 * All rights reserved.
 * -----------------------------------------------------------------------------
 */
@Documented
@Constraint(validatedBy = EnArCharactersOnlyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnArCharactersOnly {
    String message() default EN_AR_NOT_CONTAIN_SPECIAL_CHARS;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
