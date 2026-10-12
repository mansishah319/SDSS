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

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("Male", "ذكر", "M"),
    FEMALE("Female", "أنثى", "F");

    private final String englishName;
    private final String arabicName;
    private final String code;
}
