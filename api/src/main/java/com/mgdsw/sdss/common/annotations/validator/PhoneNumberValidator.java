/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mgdsw.sdss.common.annotations.Phone;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Objects;

public class PhoneNumberValidator implements ConstraintValidator<Phone, String> {

    Phone phone;


    @Override
    public void initialize(Phone constraintAnnotation) {
        this.phone = constraintAnnotation;
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (Objects.isNull(phoneNumber) || StringUtils.isBlank(phoneNumber)) {
            return true;
        }
        try {
            Locale locale = Locale.getDefault();
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber num = phoneNumberUtil.parse(phoneNumber, locale.getCountry());

            return phoneNumberUtil.isPossibleNumber(num);
        } catch (Exception e) {
            return false;
        }
    }

}