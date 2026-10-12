/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.custom;

import com.mgdsw.sdss.common.config.security.dto.UserPrincipal;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.CryptoUtil;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Log4j2
@Configuration
@RequiredArgsConstructor
public class JpaConfig {

    private final CryptoUtil cryptoUtil;

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Check if authentication is null or not authenticated
            if (authentication == null || !authentication.isAuthenticated()) {
                return getSurveyAuthorizationId();
            }

            // Attempt to extract grpNumber from an authenticated principal
            if (authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                return Optional.ofNullable(String.valueOf(userPrincipal.getGrpNumber()));
            }

            return Optional.empty();
        };
    }

    private Optional<String> getSurveyAuthorizationId() {
        HttpServletRequest httpServletRequest = DpConstants.getHttpServletRequest();

        if (httpServletRequest == null) {
            return Optional.empty();
        }

        String surveyAuthorization = httpServletRequest.getHeader("SurveyAuthorization");
        if (surveyAuthorization == null || surveyAuthorization.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            // Decrypt the survey authorization and extract employee ID
            EmployeeInfoKeyResponse employeeInfoKeyResponse =
                    cryptoUtil.decrypt(surveyAuthorization, EmployeeInfoKeyResponse.class);

            return Optional.ofNullable(String.valueOf(employeeInfoKeyResponse.getId()));
        } catch (Exception e) {
            // Log error for debugging and gracefully return empty
            log.warn("Failed to decrypt SurveyAuthorization header: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
