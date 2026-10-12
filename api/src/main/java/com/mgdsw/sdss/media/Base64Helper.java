/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.media;

import com.mgdsw.sdss.common.infrastructure.exception.child.CustomServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;
import static com.mgdsw.sdss.common.enums.ErrorCodes.UNSUPPORTED_FILE_TYPE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */
public final class Base64Helper {

    public static String encodeToBase64(MultipartFile file) {
        try {
            return Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            throw new CustomServiceException(e.getMessage(), UNSUPPORTED_FILE_TYPE.getErrorCode());
        }
    }

    private Base64Helper() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
