/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.i18n;

import com.mgdsw.sdss.common.config.security.dto.UserPrincipal;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Locale;

import static com.mgdsw.sdss.common.constants.DpConstants.DP_TRAFFIC_DEFAULT_LOCALE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    @Override
    @NonNull
    public Locale resolveLocale(@NonNull HttpServletRequest request) {
        // Try to get the user language from the token
        String userLanguage = getUserLanguageFromToken();

        if (StringUtils.hasText(userLanguage)) {
            return new Locale(userLanguage);
        }

        // If user language is not found in the token, fallback to “Accept-Language” header.
        String headerLanguage = request.getHeader("Accept-Language");
        if (StringUtils.hasText(headerLanguage)) {
            return Locale.lookup(Locale.LanguageRange.parse(headerLanguage), Arrays.asList(Locale.getAvailableLocales()));
        }

        // If no "Accept-Language” header is present, use the default locale
        return DP_TRAFFIC_DEFAULT_LOCALE;
    }

    private String getUserLanguageFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            // Retrieve user language from token or user principal
            if (userPrincipal.getLanguage() != null) {
                return userPrincipal.getLanguage().getCode();
            }
        }
        return DP_TRAFFIC_DEFAULT_LOCALE.getLanguage();
    }
}