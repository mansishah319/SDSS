/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.common.helper.pdf;

import com.mgdsw.sdss.enums.Language;

@FunctionalInterface
public interface PdfGenerator {

    byte[] createPdf(String template, Object data, Language language);
}

