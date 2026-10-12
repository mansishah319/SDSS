/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.COMMA;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.EMPTY_STRING;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 7月, 2024
 */
@Converter(autoApply = true)
public class IntegerListToStringConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return EMPTY_STRING;
        }
        return attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(COMMA));
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(dbData.split(COMMA))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}