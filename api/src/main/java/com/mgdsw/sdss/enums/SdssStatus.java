/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.enums;

import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Getter
@AllArgsConstructor
public enum SdssStatus {

    PENDING("Pending", "معلق"),
    IN_PROGRESS("In Progress", "قيد التنفيذ"),
    COMPLETED("Completed", "مكتمل"),
    DONE("Done", "تم"),
    CANCELED("Canceled", "ملغى"),
    FAILED("Failed", "فشل"),
    ON_HOLD("On Hold", "معلق"),
    REVIEW("Under Review", "قيد المراجعة");

    private final String en;
    private final String ar;

    public static List<String> getAllByLanguage() {
        return Arrays.stream(SdssStatus.values())
                .map(type -> DpCommonUtil.isCurrentUserLanguageIsEnglish() ? type.getEn() : type.getAr())
                .collect(Collectors.toList());
    }

    public static SdssStatus fromString(String text) {
        return Arrays.stream(SdssStatus.values())
                .filter(type -> type.getEn().equalsIgnoreCase(text) || type.getAr().equalsIgnoreCase(text) || type.name().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new CustomDataException(
                        String.format(
                                LocalizedErrorCodes.WRONG_STATUS_ERROR_MESSAGES.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), getAllByLanguage()),
                        LocalizedErrorCodes.WRONG_STATUS_ERROR_MESSAGES.getErrorCode()
                ));
    }
}
