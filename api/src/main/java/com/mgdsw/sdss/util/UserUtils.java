/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.util;

import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
public final class UserUtils {

    public static boolean isUserLanguageEnglish(User user) {
        return user.getLanguage().getCode().equalsIgnoreCase(Language.EN.getCode());
    }

    /**
     * Generates an array of valid email addresses from user details.
     *
     * @param user The user object containing email and alternate email.
     * @return An array of valid email addresses (primary and alternate).
     */
    public static String[] getUserEmails(User user) {
        List<String> emailList = new ArrayList<>();

        // Add primary email if present
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailList.add(user.getEmail());
        }

        // Add alternate email if present
        if (user.getAlternativeEmail() != null && !user.getAlternativeEmail().isEmpty()) {
            emailList.add(user.getAlternativeEmail());
        }

        // Convert the list to an array and return
        return emailList.toArray(new String[0]);
    }

    public static final String EXCEL_SHEET_NAME = "Users_Sheet_";
    public static final String USER_CREATION_AND_UPDATE_EXAMPLE = "{\n" +
            "  \"grpNumber\": 37075,\n" +
            "  \"language\": \"AR\",\n" +
            "  \"firstname\": \"Mohammed\",\n" +
            "  \"lastname\": \"Abdu\",\n" +
            "  \"email\": \"mohammed.elsaeed2@gmail.com\",\n" +
            "  \"mobile\": \"0501414755\",\n" +
            "  \"internalDocuments\": true,\n" +
            "  \"chatContents\": true,\n" +
            "  \"socialMediaFeeds\": true,\n" +
            "  \"decision\": true,\n" +
            "  \"questionView\": true,\n" +
            "  \"scrollingText\": true,\n" +
            "  \"usersExpertises\": [\n" +
            "    {\n" +
            "      \"areaOfExpertisesId\": 9650,\n" +
            "      \"expertLevel\": \"Expert\",\n" +
            "      \"isPrimary\": true\n" +
            "    }\n" +
            "  ],\n" +
            "  \"roles\": [\n" +
            "    41\n" +
            "  ]\n" +
            "}";

    public static final class Fields {

        public static final String USER = "user";
        public static final String FULL_USER_NAME = "fullName";
        public static final String USER_CONFIRMATION_URL = "userUrl";

        private Fields() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    private static final String[] userRecordsNames = {
            "id",
            "Title",
            "Full Name",
            "Created By",
            "Created At",
            "Updated By",
            "Updated At",
            "GRP Number",
            "Language",
            "Status",
            "Mobile",
            "Email",
            "Type",
            "Roles",
            "ExpertLevel",
            "Grade",
            "Sector",
            "Department",
            "Department Type",
            "sector Short Name"
    };

    public static String[] retrieveUserRecordsNames() {
        return Arrays.copyOf(userRecordsNames, userRecordsNames.length);
    }

    public static String computeFullName(String firstname, String lastname) {
        return firstname + " " + lastname;
    }

    private UserUtils() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
