/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
@Getter
@AllArgsConstructor
public enum Templates {

    USER_TEMPLATE_CREATION_EN("user_invitation_template_en.ftl", "user/user_invitation_template_en.ftl"),
    USER_TEMPLATE_CREATION_AR("user_invitation_template_ar.ftl", "user/user_invitation_template_ar.ftl"),

    CHALLENGE_USER_INVITATION_TEMPLATE("challenge_invitation.ftl", "challenge/challenge_invitation.ftl"),
    REMINDER_CHALLENGE_USER_INVITATION_TEMPLATE("reminder_challenge_invitation.ftl", "reminder/reminder_challenge_invitation.ftl"),

    VALIDATOR_CHALLENGE_INVITATION_TEMPLATE("validator_challenge_invitation.ftl", "validator/validator_challenge_invitation.ftl"),
    VALIDATOR_CHALLENGE_NEW_TEMPLATE("validator_challenge_new.ftl", "validator/validator_challenge_new.ftl"),

    WELCOME_CHALLENGE_TEMPLATE("welcome_challenge.ftl", "welcome/welcome_challenge.ftl"),
    VALIDATOR_NEW_VALIDATE_REMINDER_TEMPLATE("validate_challenge_template.ftl", "validator/validate_challenge_template.ftl"),

    SURVEY_INVITATION("survey_invitation.ftl", "surveys/survey_invitation.ftl"),
    SURVEY_STAGE_DONE("survey_stage_done.ftl", "surveys/survey_stage_done.ftl"),
    SURVEY_LAST_STAGE_DONE("survey_last_stage_done.ftl", "surveys/survey_last_stage_done.ftl"),

    DP_LOGO("logo.ico", "classpath:/static/images/favicon.ico");

    private final String name;
    private final String path;
}
