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
public enum EmailSubject {
    SDSS_USER_CREATION_SUBJECT("Smart Decision Support System (SDSS) Activation", "تفعيل حسابك بنظام دعم القرار الذكي"),
    CHALLENGE_USER_CREATION_SUBJECT("Smart Decision Support System (SDSS) Challenge Invitation", "قبول دعوة تحدي بنظام دعم القرار الذكي"),
    VALIDATE_CHALLENGE_SUBJECT("Smart Decision Support System (SDSS) Challenge Validation", "التحقق من التحدي لنظام دعم القرار الذكي"),
    CHALLENGE_INVITATION_REMINDER_SUBJECT("Challenge Reminder", "تذكير بحضور التحدي"),
    WELCOME_SUBJECT("Welcome Our Expert User", "الترحيب بمستخدمنا الخبير"),
    VALIDATOR_NEW_VALIDATE_REMINDER_SUBJECT("New Challenge Assigned: Your Review is Required", "تحدي جديد تم تعيينه: يُرجى مراجعته"),
    COMMANDER_NEW_VALIDATE_REMINDER_SUBJECT("Action Needed: A New Challenge Awaits Your Attention", "إجراء مطلوب: تحدي جديد ينتظر اهتمامك");

    private final String en;
    private final String ar;
}
