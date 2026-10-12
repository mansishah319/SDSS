/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.infrastructure.exception.child;

import com.mgdsw.sdss.common.infrastructure.exception.CustomException;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
public class CustomValidationException extends CustomException {

    public CustomValidationException(String message, String errorCode) {
        super(message, errorCode);
    }
}
