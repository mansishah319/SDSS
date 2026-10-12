/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
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
 * @createdOn Sep, 2023
 */

@Getter
@AllArgsConstructor
public enum ExpertLevel {

    EXPERT("Expert", "خبير"),
    INTERMEDIATE("Intermediate", "متوسط الخبرة"),
    BASIC("Basic", "مبتدئ الخبرة"),
    NONE("None", "بدون خبرة");

    private final String englishName;
    private final String arabicName;

    public static ExpertLevel fromString(String text) {
        return Arrays.stream(ExpertLevel.values())
                .filter(type -> type.getEnglishName().equalsIgnoreCase(text) || type.getArabicName().equalsIgnoreCase(text) || type.name().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new CustomDataException(
                        String.format(
                                LocalizedErrorCodes.WRONG_STATUS_ERROR_MESSAGES.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), getAllByLanguage()),
                        LocalizedErrorCodes.WRONG_STATUS_ERROR_MESSAGES.getErrorCode()
                ));
    }

    public static List<String> getAllByLanguage() {
        return Arrays.stream(ExpertLevel.values())
                .map(type -> DpCommonUtil.isCurrentUserLanguageIsEnglish() ? type.getEnglishName() : type.getArabicName())
                .collect(Collectors.toList());
    }
}
