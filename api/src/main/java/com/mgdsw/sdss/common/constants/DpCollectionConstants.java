/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.constants;

import java.util.Arrays;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

public final class DpCollectionConstants {

    public static boolean arrayContains(String[] array, String target) {
        return Arrays.asList(array).contains(target);
    }

    public static boolean stringExistInArray(String target, String[] array) {
        return Arrays.stream(array).anyMatch(target::contains);
    }

    private DpCollectionConstants() {
        throw new IllegalStateException(DpConstants.UTILITY_CLASS);
    }
}
