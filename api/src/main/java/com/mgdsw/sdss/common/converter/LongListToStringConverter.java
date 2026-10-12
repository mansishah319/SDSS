/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.converter;

import javax.persistence.Converter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 7月, 2024
 */

@Converter(autoApply = true)
public class LongListToStringConverter extends GenericListToStringConverter<Long> {

    @Override
    protected String convertToString(Long value) {
        return String.valueOf(value);
    }

    @Override
    protected Long convertToType(String value) {
        return Long.parseLong(value);
    }
}