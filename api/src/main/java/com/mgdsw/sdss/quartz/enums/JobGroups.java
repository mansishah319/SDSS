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
public enum JobGroups {

    REMINDER_INVITATION_EMAIL("challenge-reminder-jobs", "Send Challenge Reminder for all invited Users"),
    SURVEY_INVITATION_EMAIL("survey-invitation-jobs", "Send Survey Reminder for all invited Grp Employees");

    private final String jobName;
    private final String jobDescription;
}
