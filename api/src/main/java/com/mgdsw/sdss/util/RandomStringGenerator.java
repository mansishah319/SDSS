/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

import static com.mgdsw.sdss.common.constants.DpConstants.Regex.ALPHA_NUMERIC_STRING;
import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Slf4j
public final class RandomStringGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String getAlphaNumericString(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = RANDOM.nextInt(ALPHA_NUMERIC_STRING.length());
            sb.append(ALPHA_NUMERIC_STRING.charAt(index));
        }
        return sb.toString();
    }

    public static SecureRandom random() {
        return RANDOM;
    }

    private RandomStringGenerator() {
        log.info(UTILITY_CLASS);
    }
}
