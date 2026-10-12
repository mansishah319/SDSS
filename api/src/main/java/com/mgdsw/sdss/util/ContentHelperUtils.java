/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.util;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
public final class ContentHelperUtils {

    public static final String EXCEL_SHEET_NAME = "Challenges_Sheet_";

    public static final class Email {

        public static final String CHALLENGE = "challenge";
        public static final String CHALLENGE_DATE = "date";
        public static final String CHALLENGE_START_TIME = "startTime";
        public static final String CHALLENGE_END_TIME = "endTime";
        public static final String CHALLENGE_TITLE = "title";
        public static final String CHALLENGE_USER_INVITATION_URL = "ChallengeUserInvitationUrl";
        public static final String INVITATION_URL = "InvitationUrl";
        public static final String USER = "user";
        public static final String SDSS_INVITED_USER = "invitedUser";
        public static final String CONTENT_EN = "contentEn";
        public static final String CONTENT_AR = "contentAr";
        public static final String CHALLENGE_SESSION_LINK = "ChallengeSessionLink";
        public static final String MY_CHALLENGES_URL = "myChallengesUrl";
        public static final String SURVEY = "survey";
        public static final String SDSS_SURVEY_ROUND = "round";
        public static final String SURVEY_SESSION_LINK = "surveySessionLink";

        private Email() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    private ContentHelperUtils() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
