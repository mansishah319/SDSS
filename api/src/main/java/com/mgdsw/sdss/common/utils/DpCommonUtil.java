/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.utils;

import com.mgdsw.sdss.common.config.security.dto.UserPrincipal;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.enums.Country;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.DASH;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SLASH;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.UNDER_SCORE;
import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
public final class DpCommonUtil {

    public static String getSelectedLanguage() {
        return LocaleContextHolder.getLocale().getLanguage();
    }

    public static String getFormattedDate(LocalDateTime dateTime) {
        return getFormattedDate(dateTime, getCurrentLocale());
    }

    public static String getFormattedDate(LocalDateTime dateTime, Locale locale) {
        if (dateTime != null) {
            if (Language.AR.getCode().equals(locale.getLanguage())) {
                Locale arabicLocale = new Locale(Language.AR.getCode(), Country.SAUDI_ARABIA.getCountryCode());
                DecimalStyle arabicDecimalStyle = DecimalStyle.of(arabicLocale)
                        .withZeroDigit(DpConstants.Characters.Unicode.ARABIC_ZERO);

                DateTimeFormatter arabicDateFormatter = DateTimeFormatter
                        .ofPattern(DpConstants.DateFormat.DATETIME_PATTERN)
                        .withLocale(arabicLocale)
                        .withDecimalStyle(arabicDecimalStyle);

                return dateTime.format(arabicDateFormatter);
            } else {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DpConstants.DateFormat.DATETIME_PATTERN, locale);
                return dateTime.format(dateFormatter);
            }
        } else {
            return "";
        }
    }

    public static String getBaseUrlWithContext(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String contextPath = request.getContextPath();
        // Get the base URL by removing the context path and other parts after the base URL.
        return requestURL.delete(requestURL.length() - request.getRequestURI().length(), requestURL.length())
                .append(contextPath)
                .append(SLASH)
                .toString();
    }

    public static String getBaseUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String baseUrl = requestURL.substring(0, requestURL.indexOf(request.getRequestURI()));
        return baseUrl + SLASH;
    }


    public static boolean isCurrentUserLanguageIsEnglish() {
        return getUserCurrentLocale().equals(Locale.US);
    }

    public static String getFullNameWithoutIdSplit(String fullName) {
        String[] parts = fullName.split(UNDER_SCORE, 2);
        return parts.length > 1 ? parts[1] : fullName;
    }

    public static String prepareShortDepartmentNameForResponse(String departmentName, Long departmentTypeCode, Long parentDepartmentTypeCode) {
        return Optional.ofNullable(departmentName)
                .map(name -> {
                    try {
                        String substring = name.substring(name.indexOf(DASH) + 2, name.lastIndexOf(DASH));
                        switch (departmentTypeCode.intValue()) {
                            case 1:
                                return name.substring(0, name.indexOf(DASH));
                            case 22:
                                if (!name.startsWith("مكتب")) {
                                    return name.substring(0, name.indexOf(DASH));
                                }
                                break;
                            case 40:
                            case 60:
                                return substring;
                            case 3:
                                if (parentDepartmentTypeCode != 120) {
                                    return name.substring(name.lastIndexOf(DASH) + 1);
                                } else {
                                    return substring;
                                }
                            default:
                                return name.replace("- مدير المخفر", "")
                                        .replace("- مدير إدارة فرعية", "")
                                        .replace("- مدير ادارة فرعية", "")
                                        .replace("- مدير مركز", "")
                                        .replace("- رئيس قسم", "")
                                        .replace("- مدير الادارة", "")
                                        .replace("- مدير المركز", "");
                        }
                    } catch (Exception e) {
                        return name;
                    }
                    return name;
                }).orElse(null);
    }

    public static String getShortDepartmentName(String departmentName) {
        return Optional.ofNullable(departmentName)
                .filter(name -> !name.isEmpty())
                .map(name -> name.split("-")[0].trim())
                .orElse(departmentName);
    }

    public static String retrieveLocalValue(String en, String ar) {
        return isCurrentUserLanguageIsEnglish() ? en : ar;
    }

    public static String retrievePublicLocalValue(String en, String ar) {
        return isCurrentPublicUserLanguageIsEnglish() ? en : ar;
    }

    public static boolean isCurrentPublicUserLanguageIsEnglish() {
        return getUserCurrentPublicUserLocale().equals(Language.EN.getCode());
    }

    public static Locale getCurrentUserLocaleForPublicApis() {
        return new Locale(getUserCurrentPublicUserLocale());
    }

    public static String getUserCurrentPublicUserLocale() {
        HttpServletRequest request = getCurrentPublicRequest();

        if (request != null) {
            String acceptLanguage = request.getHeader("Accept-Language");
            if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
                return acceptLanguage.split("[,;]")[0].trim();
            }
        }

        // Fallback to the default language
        return Locale.getDefault().getLanguage();
    }

    public static HttpServletRequest getCurrentPublicRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    public static UserPrincipal getUserPayload() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long retrieveCurrentUserGrpNumber() {
        return getUserPayload().getGrpNumber();
    }

    public static boolean isTheCurrentUserAllowedToDoThisProcess(String createdBy) {
        return Long.valueOf(createdBy).equals(retrieveCurrentUserGrpNumber());
    }

    public static Set<String> retrieveCurrentUserAuthorities() {
        return getUserPayload().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public static Locale getUserCurrentLocale() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Language language = userPrincipal.getLanguage();
        return language.equals(Language.EN) ? Locale.US : new Locale("ar");
    }

    public static Language getLanguageFromLocale(Locale locale) {
        return locale.equals(Locale.US) ? Language.EN : Language.AR;
    }

    public static Locale getUserLocaleBasedOnCurrentUser(User currentUser) {
        Locale userLocale = new Locale(currentUser.getLanguage().getCode());
        log.info("User Language is -> {}", userLocale);
        return userLocale;
    }

    private static Locale getCurrentLocale() {
        return Locale.getDefault();
    }

    private DpCommonUtil() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
