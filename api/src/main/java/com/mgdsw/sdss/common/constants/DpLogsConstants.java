/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.constants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
public final class DpLogsConstants {

    public static final String PAGINATION_RESPONSE_STATUS = "Pagination Response Status is : {}";

    private DpLogsConstants() {
        throw new IllegalStateException(DpConstants.UTILITY_CLASS);
    }
}
