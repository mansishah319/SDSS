/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.utils;

import com.mgdsw.sdss.common.constants.DpConstants;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public final class FieldNamesUtil {

    public static final String USER_NAME = "{USER_NAME}";
    public static final String PASSWORD = "{PASSWORD}";
    public static final String FIRST_NAME = "{FIRST_NAME}";
    public static final String LAST_NAME = "{LAST_NAME}";
    public static final String ARABIC_NAME = "{ARABIC_NAME}";
    public static final String DESCRIPTION_ARABIC = "{DESCRIPTION_ARABIC}";
    public static final String DESCRIPTION_ENGLISH = "{DESCRIPTION_ENGLISH}";
    public static final String ENGLISH_NAME = "{ENGLISH_NAME}";
    public static final String MOBILE_NUMBER = "{MOBILE_NUMBER}";
    public static final String START_TIME = "{START_TIME}";
    public static final String END_TIME = "{END_TIME}";
    public static final String AREA_EXPERTISES_ID = "{AREA_EXPERTISES_ID}";
    public static final String FIELD_AREA_EXPERTISES_ID = "areaOfExpertisesId";
    public static final String DEPARTMENT = "{DEPARTMENT}";
    public static final String EXPERTISES = "{EXPERTISES}";
    public static final String EMAIL = "{EMAIL}";
    public static final String ALTERNATIVE_EMAIL = "{ALTERNATIVE_EMAIL}";
    public static final String ALTERNATIVE_MOBILE_NUMBER = "{ALTERNATIVE_MOBILE_NUMBER}";
    public static final String LANGUAGE = "{LANGUAGE}";
    public static final String EXPERT_LEVEL = "{EXPERT_LEVEL}";
    public static final String ROLES = "{ROLES}";
    public static final String SECTOR_ID = "{SECTOR_ID}";
    public static final String GRP_NUMBER = "{GRP_NUMBER}";
    public static final String SOLUTION_TEXT = "{SOLUTION_TEXT}";
    public static final String CHALLENGE_ID = "{CHALLENGE_ID}";
    public static final String VOTE = "{VOTE}";
    public static final String TOPIC_CLASSIFICATION = "{TOPIC_CLASSIFICATION}";
    public static final String LEVEL_OF_DECISION = "{LEVEL_OF_DECISION}";
    public static final String IMPORTANCE_OF_DECISION = "{IMPORTANCE_OF_DECISION}";
    public static final String IMPACT = "{IMPACT}";
    public static final String FIELD = "{FIELD}";
    public static final String NAME_AR = "{NAME_AR}";
    public static final String NAME_EN = "{NAME_EN}";
    public static final String CHALLENGE_SETTINGS_TYPE = "{CHALLENGE_SETTINGS_TYPE}";
    public static final String STRATEGIC_DIRECTION_IDS = "{STRATEGIC_DIRECTION_IDS}";
    public static final String FUTURE_STRATEGIC_DIRECTION_ID = "{FUTURE_STRATEGIC_DIRECTION_ID}";
    public static final String STRATEGIC_GOALS_ID = "{STRATEGIC_GOALS_ID}";
    public static final String STRATEGIC_DIRECTION_ID = "{STRATEGIC_DIRECTION_ID}";
    public static final String FUTURE_STRATEGIC_GOALS_ID = "{FUTURE_STRATEGIC_GOALS_ID}";
    public static final String SECTOR_NAME = "{SECTOR_NAME}";
    public static final String MODERATOR_ID = "{MODERATOR_ID}";
    public static final String ASSISTANT_MODERATOR_GRP = "{ASSISTANT_MODERATOR_GRP}";
    public static final String COMMITTEE_ID = "{COMMITTEE_ID}";
    public static final String COMMITTEE_NAME = "{COMMITTEE_NAME}";
    public static final String DEPARTMENT_ID = "{DEPARTMENT_ID}";
    public static final String DEPARTMENT_NAME = "{DEPARTMENT_NAME}";
    public static final String VISIBILITY = "{VISIBILITY}";
    public static final String ALLOW_IMMEDIATE_ACCESS = "{ALLOW_IMMEDIATE_ACCESS}";
    public static final String URGENT = "{URGENT}";
    public static final String TITLE = "{TITLE}";
    public static final String SUBJECT_AR = "{SUBJECT_AR}";
    public static final String CONTENT_AR = "{CONTENT_AR}";
    public static final String SUBJECT_EN = "{SUBJECT_EN}";
    public static final String CONTENT_EN = "{CONTENT_EN}";
    public static final String TEMPLATES_TYPE = "{TEMPLATES_TYPE}";

    public static final String ROUND_NUMBER = "{ROUND_NUMBER}";
    public static final String ROUND_ID = "{ROUND_ID}";
    public static final String SURVEY_ID = "{SURVEY_ID}";
    public static final String ROUND_STATUS = "{ROUND_STATUS}";
    public static final String SURVEY_STATUS = "{SURVEY_STATUS}";

    public static final String TITLE_EN = "{TITLE_EN}";
    public static final String TITLE_AR = "{TITLE_AR}";
    public static final String QUESTION_TYPE = "{QUESTION_TYPE}";
    public static final String OPTION_NAME_ARABIC = "{OPTION_NAME_ARABIC}";
    public static final String OPTION_NAME_ENGLISH = "{OPTION_NAME_ENGLISH}";

    // Real Names
    public static final String TOTAL_COUNT = "totalCount";
    public static final String TRUE_COUNT = "trueCount";
    public static final String FALSE_COUNT = "falseCount";
    public static final String ROUND_DEFAULT_TITLE_EN = "Round";
    public static final String ROUND_DEFAULT_TITLE_AR = "المرحلة";
    public static final String ROUND_DEFAULT_DESCRIPTION_TITLE_EN = "A manual round allows responses to be reviewed and adjusted before proceeding, ensuring accuracy.";
    public static final String ROUND_DEFAULT_DESCRIPTION_TITLE_AR = "تسمح الجولة اليدوية بمراجعة الاستجابات وتعديلها قبل المتابعة لضمان الدقة.";

    private FieldNamesUtil() {
        throw new IllegalStateException(DpConstants.UTILITY_CLASS);
    }
}
