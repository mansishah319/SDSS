/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Dec, 2023
 */

@Getter
@AllArgsConstructor
public enum ChallengeInvitationType {

    VALIDATION("Validators Review", "مراجعة المدققين"),

    ADMIN_USER("Main Added Admin User", "إضافة مستخدم إداري رئيسي"),
    GUEST_USER("Main Added Guest User", "إضافة مستخدم ضيف رئيسي"),

    ADMIN_TEMPORARY("Admin User added during the session", "إضافة مستخدم إداري أثناء الجلسة"),
    GUEST_TEMPORARY("Guest User added during the session", "إضافة مستخدم ضيف أثناء الجلسة");

    private final String enCamelCaseName;
    private final String arCamelCaseName;
}
