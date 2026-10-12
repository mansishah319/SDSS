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
 * @createdOn Sep, 2023
 */
public class CustomForbiddenException extends CustomException {

    private static final long serialVersionUID = 7168555867098356326L;

    public CustomForbiddenException(String message, String errorCode) {
        super(message, errorCode);
    }
}
