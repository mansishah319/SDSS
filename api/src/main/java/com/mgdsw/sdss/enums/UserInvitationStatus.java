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
 * @createdOn Aug, 2023
 */

@Getter
@AllArgsConstructor
public enum UserInvitationStatus {

    PENDING("Pending", "معلق"),
    ACCEPTED("Accepted", "تم القبول"),
    REJECTED("Rejected", "تم الرفض"),
    DELETED("Deleted", "تم الحذف"),
    UNDER_REVIEW("Under Review", "تحت المراجعة");

    private final String englishName;
    private final String arabicName;
}
