/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.utils;

import lombok.extern.slf4j.Slf4j;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Slf4j
public final class TranslatorUtil {

    public static final class Message {
        public static final String NOT_FOUND = "message.error.not.found";

        private Message() {
            log.info(UTILITY_CLASS);
        }
    }

    public static final class ClassFiledNames {

        public static final class StrategicDirection {

            public static final String STRATEGIC_DIRECTION_TITLE = "class.strategic_goal_direction.name";

            private StrategicDirection() {
                log.info(UTILITY_CLASS);
            }
        }

        public static final class Users {

            public static final String USERS_TITLE = "class.user.name";
            public static final String CHALLENGE_SETTINGS_TITLE = "class.challenge.settings.name";
            public static final String CORE_SERVICE = "core.service.name";
            public static final String COMMITTEE_SERVICE = "committee.service.name";

            private Users() {
                log.info(UTILITY_CLASS);
            }
        }

        private ClassFiledNames() {
            log.info(UTILITY_CLASS);
        }
    }

    private TranslatorUtil() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
