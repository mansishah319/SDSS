/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.utils;

import com.mgdsw.sdss.common.constants.DpConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.COMMA;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.EMPTY_STRING;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */
public final class ListActionsUtil {

    public static List<String> convert(String str) {
        return (Objects.isNull(str) || str.isEmpty()) ?
                new ArrayList<>() :
                Arrays.stream(str.split(COMMA))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public static String convert(List<String> strList) {
        return (Objects.isNull(strList) || strList.isEmpty()) ? EMPTY_STRING : String.join(COMMA, strList);
    }

    private ListActionsUtil() {
        throw new IllegalStateException(DpConstants.UTILITY_CLASS);
    }
}
