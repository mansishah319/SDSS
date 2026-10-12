/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.logging.filter;

import com.mgdsw.sdss.common.config.security.dto.UserPrincipal;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MDC_USER_ID_KEY;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Log4j2
@Component
public class MDCLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initializing MDCLoggingFilter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            MDC.put(MDC_USER_ID_KEY, String.valueOf(userPrincipal.getGrpNumber()));
        } else {
            MDC.remove(MDC_USER_ID_KEY);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_USER_ID_KEY);
        }
    }

    @Override
    public void destroy() {
        log.info("Destroying MDCLoggingFilter");
    }
}