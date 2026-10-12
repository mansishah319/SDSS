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
 * @createdOn Aug, 2023
 */

@Getter
@AllArgsConstructor
public enum Status {

    INACTIVE("Inactive", "غير نشط"),
    ACTIVE("Active", "نشط"),
    DELETED("Deleted", "محذوف");

    private final String nameEn;
    private final String nameAr;
}
