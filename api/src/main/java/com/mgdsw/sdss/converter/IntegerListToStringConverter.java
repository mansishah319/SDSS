/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.converter;

import javax.persistence.Converter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 7月, 2024
 */
@Converter(autoApply = true)
public class IntegerListToStringConverter extends GenericListToStringConverter<Integer> {

    @Override
    protected String convertToString(Integer value) {
        return String.valueOf(value);
    }

    @Override
    protected Integer convertToType(String value) {
        return Integer.parseInt(value);
    }
}