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
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */
public final class FallbackGeneralMethodsUtil {

    public static <T> List<T> retrieveListGenericFallback() {
        return new ArrayList<>();
    }

    public static <T> T retrieveObjectGenericFallback() {
        return null;
    }

    private FallbackGeneralMethodsUtil() {
        throw new IllegalStateException(DpConstants.UTILITY_CLASS);
    }
}
