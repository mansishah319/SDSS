/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.common.helper.pdf.constants;

import lombok.extern.log4j.Log4j2;

import java.io.File;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

@Log4j2
public final class CustomFontConstants {
    public static final String NOTO_ARABIC_FONT = "fonts/NotoNaskhArabicRegular.ttf";
    public static final String VERDANA_FONT = "fonts/Verdana.ttf";
    public static final String FONTS_DIRECTORY = "/fonts";
    public static final String DUBAI_BOLD = "fonts/DubaiBold.ttf";
    public static final String DUBAI_REGULAR = "fonts/DubaiRegular.ttf";
    public static final String ARIAL = "fonts/arial.ttf";

    public static void verifyFontFiles() {
        String[] fonts = {NOTO_ARABIC_FONT, DUBAI_BOLD, DUBAI_REGULAR, VERDANA_FONT, ARIAL};
        for (String font : fonts) {
            File fontFile = new File(font);
            if (!fontFile.exists()) {
                log.warn("Font file not found: {}", font);
            }
        }
    }

    private CustomFontConstants() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
