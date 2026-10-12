/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.common.helper.pdf.constants;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

public final class HtmlTemplateConstants {

    public static final String REPORTS = "reports";
    public static final String SURVEY_DETAILS_REPORT_TEMPLATE = "/survey-details-report.html";
    public static final String CHALLENGE_DETAILS_REPORT_TEMPLATE = "/challenge-details-report.html";

    private HtmlTemplateConstants() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
