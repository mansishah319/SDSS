/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Getter
@AllArgsConstructor
public enum Country {

    SAUDI_ARABIA("Saudi Arabia", "SA"),
    EGYPT("Egypt", "EG"),
    UNITED_ARAB_EMIRATES("United Arab Emirates", "AE"),
    MOROCCO("Morocco", "MA");

    private final String fullName;
    private final String countryCode;
}
