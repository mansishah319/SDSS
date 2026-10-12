/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.util;

import lombok.extern.slf4j.Slf4j;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */

@Slf4j
public final class DpRolesUtil {

    public static final String ADMIN_ONLY_PREAUTHORIZE = "hasAnyRole('ADMIN', 'COMMANDER', 'PORTALADMIN', 'PORTALROLECOMMANDER')";
    public static final String ADMIN_MODERATOR_PREAUTHORIZE = "hasAnyRole('ADMIN', 'MODERATOR', 'COMMANDER', 'PORTALADMIN', 'PORTALROLECOMMANDER', 'PORTALROLEMODERATOR')";
    public static final String CHALLENGES_ADMIN_VALIDATOR_MODERATOR_PREAUTHORIZE = "hasAnyRole('ADMIN', 'MODERATOR', 'VALIDATOR', 'COMMANDER', 'PORTALADMIN', 'PORTALROLECOMMANDER', 'PORTALROLEMODERATOR', 'PORTALROLEVALIDATOR')";

    private DpRolesUtil() {
        log.info(UTILITY_CLASS);
    }
}
