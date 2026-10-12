/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.constants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
public final class DpConstants {

    public static final String UTILITY_CLASS = "Utility class";
    public static final Locale DP_TRAFFIC_DEFAULT_LOCALE = new Locale("ar");

    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        log.debug("Not called in the context of an HTTP request");
        return null;
    }

    public static class DateFormat {
        public static final String DATE_PATTERN = "yyyy-MM-dd";
        public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
        public static final String DUBAI_ZONE_ID = "Asia/Dubai";
        public static final String ZEROS_OF_HOUR_MIN_SEC = "00:00:00";

        private DateFormat() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    public static class Country {

        public static List<Locale> getExpectedLocals() {
            return Arrays.asList(new Locale("en"), new Locale("ar"));
        }

        private Country() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    public static final class Regex {

        public static final String EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        public static final String START_EN_WORDS_AND_NUMBERS_BETWEEN = "^[a-zA-Z][a-zA-Z0-9]*$";
        public static final String ARABIC_WORDS_AND_NUMBERS_ONLY = "^[\\u0621-\\u064A0-9 ]+$";
        //        QUESTION_ALLOWED_CHARS = "?- .,/_\\*#&"
        public static final String SPACE_HYPHEN_AND_ALLOWED_CHARS = "- &";
        public static final String ARABIC_REGEX = "^[\\p{InArabic}\\p{N}\\w\\s{allowed}]*$";
        public static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        public static final String CAPITAL_LETTERS = "^[A-Z]+$";
        public static final String HTML_PATTERN = ".*<[^>]+>.*";

        public static final class Message {

            public static final String START_EN_WORDS_AND_NUMBERS_BETWEEN = "Only English words and numbers are allowed, and it must start with an English word";
            public static final String ARABIC_WORDS_AND_NUMBERS_ONLY = "Only Arabic words and numbers are allowed";

            private Message() {
                log.info(UTILITY_CLASS);
            }
        }

        private Regex() {
            log.info(UTILITY_CLASS);
        }
    }

    public static class Messages {

        public static class SuccessMessages {

            public static final String MESSAGES = "i18n/messages";
            public static final String CREATED_SUCCESSFULLY = "Created successfully";
            public static final String UPDATED_SUCCESSFULLY = "Updated successfully";
            public static final String DELETED_SUCCESSFULLY = "DELETED_SUCCESSFULLY";
            public static final String DATA_RETRIEVED_SUCCESSFULLY = "Data retrieved successfully.";
            public static final String UPLOADED_SUCCESSFULLY = "Uploaded successfully.";
            public static final String RECORD_FOUND = "Record found.";
            public static final String SDSS_INVITATION_CONFIRMED_SUCCESSFULLY = "Email address has been successfully confirmed";
            public static final String OPERATION_COMPLETED = "Operation completed successfully.";
            public static final String RESOURCE_AVAILABLE = "Requested resource is available.";
            public static final String LOGGED_IN_SUCCESSFULLY = "Logged in successfully";
            public static final String LOGGED_OUT_SUCCESSFULLY = "Logged out successfully";
            public static final String PAGINATION_LOGGED_MESSAGE = "Total pages: {}";
            public static final String PASSWORD_CHANGED_SUCCESSFULLY = "Password changed successfully";
            public static final String PROFILE_UPDATED_SUCCESSFULLY = "Profile updated successfully";
            public static final String RECORD_APPROVED_SUCCESSFULLY = "Record approved successfully";

            private SuccessMessages() {
                throw new IllegalStateException(UTILITY_CLASS);
            }
        }

        public static class FailedMessages {
            public static final String INVALID_USERNAME_OR_PASSWORD = "{INVALID_USERNAME_OR_PASSWORD}";
            public static final String NOT_NULL_ERROR_MESSAGE = "{NOT_NULL_ERROR_MESSAGE}";
            public static final String NOT_BLANK_ERROR_MESSAGE = "{NOT_BLANK_ERROR_MESSAGE}";
            public static final String NOT_EMPTY_ERROR_MESSAGE = "{NOT_EMPTY_ERROR_MESSAGE}";
            public static final String CAPITAL_LETTERS_REQUIRED_ERROR_MESSAGE = "{CAPITAL_LETTERS_REQUIRED_ERROR_MESSAGE}";
            public static final String INVALID_INPUT = "{INVALID_INPUT}";
            public static final String RESOURCE_NOT_FOUND = "{RESOURCE_NOT_FOUND}";
            public static final String VALIDATE_SESSION = "{VALIDATE_SESSION}";
            public static final String DUPLICATE_RECORD = "{DUPLICATE_RECORD}";
            public static final String OPERATION_FAILED = "{OPERATION_FAILED}";
            public static final String UNAUTHORIZED_ACCESS = "{UNAUTHORIZED_ACCESS}";
            public static final String SERVER_ERROR = "{SERVER_ERROR}";
            public static final String VALIDATION_FAILED = "{VALIDATION_FAILED}";
            public static final String FAILED_TO_DELETE_DIR = "{FAILED_TO_DELETE_DIR}";
            public static final String INVALID_EMAIL = "{INVALID_EMAIL}";
            public static final String INVALID_PHONE = "{INVALID_PHONE}";
            public static final String EN_AR_NOT_CONTAIN_SPECIAL_CHARS = "{en.ar.must.not.contain.special.characters}";
            public static final String START_END_TIME_ERROR = "{START_END_TIME_ERROR}";
            public static final String START_TIME_FUTURE_ERROR = "{START_TIME_FUTURE_ERROR}";
            public static final String END_TIME_FUTURE_ERROR = "{END_TIME_FUTURE_ERROR}";
            public static final String REMINDERS_TIME_FUTURE_ERROR = "{REMINDERS_TIME_FUTURE_ERROR}";
            public static final String AUTOMATIC_ROUND_MANDATORY_FIELDS = "{AUTOMATIC_ROUND_MANDATORY_FIELDS}";
            public static final String JOB_LIST_MUST_CONTAIN_VALUE_WHILE_DP_NOT_EMPTY = "{JOB_LIST_MUST_CONTAIN_VALUE_WHILE_DP_NOT_EMPTY}";

            private FailedMessages() {
                throw new IllegalStateException(UTILITY_CLASS);
            }
        }

        private Messages() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    public static final class Urls {
        public static final String CONFIRM_ACCOUNT = "/confirm-account";
        public static final String ACCEPT_INVITATION = "/accept-invitation";
        public static final String HASH_KEY_PARAM = "hashKey=";
        public static final String CONFIRMATION_TOKEN_PARAM = "confirmationToken=";

        private Urls() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    public static final class Characters {
        public static final String SLASH = "/";
        public static final String PERCENTAGE = "%";
        public static final String COMMA = ",";
        public static final String COMMA_SPACE = ", ";
        public static final String SPACE = " ";
        public static final String SEMICOLON = ";";
        public static final String EMPTY_STRING = "";
        public static final String UNDER_SCORE = "_";
        public static final String LEFT_BRACKET = "(";
        public static final String RIGHT_BRACKET = ")";
        public static final String NEWLINE = "\n";
        public static final String DOUBLE_SLASH = "\\";
        public static final char DASH = '-';
        public static final String STAR = "*";
        public static final String DOT = ".";
        public static final String QUESTION_MARK = "?";
        public static final String UTF8 = "UTF-8";
        public static final String AND = "&";
        public static final String AND_WITH_SPACES = "&";
        public static final char HASH_CHAR = '#';
        public static final char OPEN_ARRAY = '[';
        public static final char CLOSE_ARRAY = ']';
        public static final String OPEN_BRACE_PLACEHOLDER = "${";
        public static final String CLOSE_BRACE_PLACEHOLDER = "}";
        public static final String COLON = ":";

        public static final class Unicode {

            public static final char ARABIC_ZERO = '\u0660';

            private Unicode() {
                throw new IllegalStateException(UTILITY_CLASS);
            }
        }

        private Characters() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    public static final class Keys {

        public static final String ONLINE = "online";

        private Keys() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    public static final class Authentication {

        public static final String MAIN_AUTH_SWAGGER_MODULE = "bearerAuthAbdu";
        public static final String AUTHORIZATION = "Authorization";
        public static final String BASIC = "Basic ";
        public static final String BEARER = "Bearer ";
        public static final String SECRET_KEY_SPEC = "Blowfish";
        public static final String EXCEPTION = "exception";
        public static final String MDC_USER_ID_KEY = "userId";
        public static final String GUID = "guid";
        public static final String ROLE_PREFIX = "ROLE_";
        public static final String GRP_NUMBER_NAME = "grpNumber";
        public static final String USERNAME = "userName";
        public static final String FULL_NAME = "fullName";
        public static final String ROLE_LIST_NAME = "roleList";
        public static final String IDM_ROLES = "roles";
        public static final String ROLES = "roles";

        public static String[] getWhiteListedPaths() {
            return new String[]{
                    "/ws/**", "/sdss-service/sdssWs/**", "ws://**", "/ws", "/sdssWs/**", "**/sdssWs/**", "**/sdssWs", "/sdssWs",
                    "**/sdss-service/sdssWs/**", "**/sdss-service/sdssWs", "/sdss-service/sdssWs",
                    "/actuator/**", "/sdss-service/actuator/**",
                    "/hystrix/monitor",
                    "/proxy.stream", "/actuator/hystrix.stream",
                    "/sdss-service/main-survey-home-screen-statistics",
                    "/sdss-service/hystrix/monitor", "/sdss-service/proxy.stream",
                    "/sdss-service/actuator/hystrix.stream",
                    "/hystrix/**", "/sdss-service/hystrix/**",
                    "/auth/**", "/**/DubaiPolice/**",
                    "/DubaiPolice/swagger-ui.html", "/DubaiPolice/v3/api-docs", "**/v3/api-docs",
                    "/DubaiPolice/**", "/**/*.ico",
                    "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg",
                    "/**/*.html", "/**/*.css", "/**/*.js", "/auth/**",
                    "/api/v1/users/profile-img/**", "/sdss-service/api/v1/users/profile-img/**",
                    "/api/v1/sdss-survey/public/**", "/sdss-service/api/v1/sdss-survey/public/**",
                    "/api/v1/public/survey-user-answers", "/sdss-service/api/v1/public/survey-user-answers",
                    "/api/v1/public/survey-user-answers/**", "/sdss-service/api/v1/public/survey-user-answers/**",
                    "/v1/public/api/v1/invited-user-survey-profile/**", "/sdss-service/v1/public/api/v1/invited-user-survey-profile/**",
                    "/v1/api/v1/survey-users/public/**", "/sdss-service/v1/api/v1/survey-users/public/**",
                    "/api/v1/users/confirm-account/**", "/sdss-service/api/v1/users/confirm-account/**",
                    "/api/v1/pdf/convert/**", "/sdss-service/api/v1/pdf/convert/**",
                    "confirm-account", "profile-img", "/sdssWs/**",
                    "/**/public/api/v1/core/sdss/login/**", "/sdss-service/public/api/v1/core/sdss/login/**"
            };
        }

        private Authentication() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    private DpConstants() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
