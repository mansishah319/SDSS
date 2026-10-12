/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.committee.constatnts;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Mar, 2024
 */

public final class CommitteeConstants {

    public static final String GET_ALL_COMMITTEES_URL = "/v1.0/getAllTeams";

    private CommitteeConstants() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
