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

import java.util.Arrays;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Getter
@AllArgsConstructor
public enum SystemRoles {

    GUEST("Guest", "ضيف", "ROLE_PORTALGUEST", "PORTALGUEST"),
    ADMIN("Admin", "أدمن", "ROLE_PORTALADMIN", "PORTALADMIN"),
    MEMBER("Member", "عضو", "ROLE_PORTALROLEMEMBER", "PORTALROLEMEMBER"),
    VALIDATOR("Validator", "ُمدقق", "ROLE_PORTALROLEVALIDATOR", "PORTALROLEVALIDATOR"),
    COMMANDER("Commander", "مدير", "ROLE_PORTALROLECOMMANDER", "PORTALROLECOMMANDER"),
    MODERATOR("Moderator", "منسق", "ROLE_PORTALROLEMODERATOR", "PORTALROLEMODERATOR");

    private final String en;
    private final String ar;
    private final String key;
    private final String tokenRole;

    public static SystemRoles fromTokenRole(String tokenRole) {
        return Arrays.stream(SystemRoles.values()) // Convert enum values to a stream
                .filter(role -> role.getTokenRole().equalsIgnoreCase(tokenRole) || role.getKey().equalsIgnoreCase(tokenRole)) // Filter by tokenRole
                .findFirst() // Find the first matching role
                .orElseThrow(() -> new IllegalArgumentException("Unknown token role: " + tokenRole)); // Throw exception if not found
    }
}
