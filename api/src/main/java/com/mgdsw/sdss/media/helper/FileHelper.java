/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.media.helper;

import com.mgdsw.sdss.common.enums.DpMediaType;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.media.util.MediaUtil;
import lombok.extern.slf4j.Slf4j;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Slf4j
public final class FileHelper {

    public static void checkBasedOnMediaType(String mediaType, String extension) {
        if (mediaType.equals(DpMediaType.IMAGE.getCamelCaseName()))
            checkImages(extension);
        else if (mediaType.equals(DpMediaType.VIDEO.getCamelCaseName()))
            checkVideos(extension);
        else
            throw new CustomDataException(
                    ErrorCodes.UNSUPPORTED_MEDIA_TYPE.getErrorCode(),
                    ErrorCodes.UNSUPPORTED_MEDIA_TYPE.getMessage());
    }

    public static void checkImages(String extension) {
        if (!MediaUtil.getAllowedImagesExtensions().contains(extension.toLowerCase()))
            throw new CustomDataException(
                    ErrorCodes.UNSUPPORTED_MEDIA_TYPE.getErrorCode(),
                    ErrorCodes.UNSUPPORTED_MEDIA_TYPE.getMessage());
    }

    public static void checkVideos(String extension) {
        if (!MediaUtil.getAllowedVideosExtensions().contains(extension.toLowerCase()))
            throw new CustomDataException(
                    ErrorCodes.UNSUPPORTED_MEDIA_TYPE.getErrorCode(),
                    ErrorCodes.UNSUPPORTED_MEDIA_TYPE.getMessage());
    }

    private FileHelper() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
