/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum DesignationType {

    RANK("Rank", "رقم"),
    POSITION("Position", "الرتبة");

    private final String englishName;
    private final String arabicName;

    private static final Map<String, DesignationType> NAME_TO_ENUM =
            Arrays.stream(values()).collect(Collectors.toMap(DesignationType::getEnglishName, Function.identity()));

    public static String name(DesignationType enumName) {
        return enumName.getEnglishName();
    }
}
