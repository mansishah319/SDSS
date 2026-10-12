/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.common.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

@Log4j2
public final class SecurityUtils {
    public static String getCurrentSessionId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            log.info("Current Session Id: {}", request.getSession().getId());
            return request.getSession().getId();
        }
        return "default"; // fallback for non-web contexts
    }

    private SecurityUtils() {
        log.info(UTILITY_CLASS);
    }
}