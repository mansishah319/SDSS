/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.SAlphaSpace;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SAlphaSpaceValidator implements ConstraintValidator<SAlphaSpace, String> {

    SAlphaSpace space;
    String message = "Must contain AlphaSpace characters : {text}";

    @Override
    public void initialize(SAlphaSpace textOnly) {
        this.space = textOnly;
    }


    public boolean isValid(String text, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(text)) {
            return true;
        }
        if (!StringUtils.isAlphaSpace(text)) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(message.replace("{text}", text))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}
