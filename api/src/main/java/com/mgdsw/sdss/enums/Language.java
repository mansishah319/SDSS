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

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Getter
@AllArgsConstructor
public enum Language {

    EN("English", "en"),
    AR("Arabic", "ar");

    private final String name;
    private final String code;

    public static Language fromString(String text) {
        return Arrays.stream(Language.values())
                .filter(type -> type.getCode().equalsIgnoreCase(text) || type.getName().equalsIgnoreCase(text) || type.name().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }
}
