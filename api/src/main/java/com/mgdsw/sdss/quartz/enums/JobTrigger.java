/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.quartz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jul, 2024
 */

@Getter
@AllArgsConstructor
public enum JobTrigger {

    REMINDER_INVITATION_EMAIL_TRIGGER("challenge-reminder-trigger", "Trigger for the Challenge Reminder"),
    SURVEY_INVITATION_EMAIL_TRIGGER("survey-invitation-trigger", "Trigger for the Survey Reminder");

    private final String triggerName;
    private final String triggerDescription;
}
