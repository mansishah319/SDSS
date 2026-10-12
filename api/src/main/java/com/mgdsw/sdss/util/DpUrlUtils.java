/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Dec, 2023
 */
@Slf4j
public final class DpUrlUtils {

    public static String getBaseUrl(HttpServletRequest request) {
        String requestUrl = getRequestUrl(request);
        try {
            URL url = new URL(requestUrl);
            return url.getProtocol() + "://" + url.getHost() + (url.getPort() > 0 ? ":" + url.getPort() : "");
        } catch (MalformedURLException e) {
            // Handle MalformedURLException according to your needs
            log.debug(e.toString(), e);
            return null;
        }
    }

    private static String getRequestUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return url.toString();
        } else {
            return url.append('?').append(queryString).toString();
        }
    }

    private DpUrlUtils() {
        log.info(UTILITY_CLASS);
    }
}
