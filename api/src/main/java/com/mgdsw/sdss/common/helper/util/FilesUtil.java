/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.helper.util;

import java.time.format.DateTimeFormatter;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
public final class FilesUtil {

    public static final String EXCEL_FILE_EXT = ".xlsx";
    public static final String PDF_FILE_EXT = ".pdf";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_DISPOSITION_AND_SUGGESTED_FILENAME = "Content-Disposition,X-Suggested-Filename";
    public static final String ATTACHMENT_NAME = "attachment; filename=";
    public static final String ACCESS_CONTROL = "Access-Control-Expose-Headers";
    public static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final String FILE_NAME_PREFIX_CONFIGURABLE = "Configurable_";
    public static final String CONTENT_TYPE = "application/octet-stream";

    /**
     * Utility class.
     */
    private FilesUtil() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}

