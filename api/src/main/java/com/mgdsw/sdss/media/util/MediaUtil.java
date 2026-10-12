/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.media.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

public class MediaUtil {

    public static final String BASE_FOLDER = "/opt/dpMedia";
    private static final List<String> ALLOWED_IMAGES_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final List<String> ALLOWED_VIDEOS_EXTENSIONS = Arrays.asList("mp4", "avi", "mkv", "mov");

    public static List<String> getAllowedImagesExtensions() {
        return Collections.unmodifiableList(ALLOWED_IMAGES_EXTENSIONS);
    }

    public static List<String> getAllowedVideosExtensions() {
        return Collections.unmodifiableList(ALLOWED_VIDEOS_EXTENSIONS);
    }

    private MediaUtil() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
