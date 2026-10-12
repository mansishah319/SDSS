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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Getter
@AllArgsConstructor
public enum QuestionType {

    SINGLE_SELECT("Single Choice", "اختيار واحد", "single-choice"),
    MULTI_SELECT("Multiple Choice", "اختيارات متعددة", "multiple-choice"),
    TRUE_FALSE("True/False", "صحيح/خاطئ", "true-false"),
    TEXT("Text", "نص", "text");

    private final String en;
    private final String ar;
    private final String key;

    public static List<String> getAllByLanguage() {
        return Arrays.stream(QuestionType.values())
                .map(type -> DpCommonUtil.isCurrentUserLanguageIsEnglish() ? type.getEn() : type.getAr())
                .collect(Collectors.toList());
    }

    public static List<Map<String, String>> getAllWithKeysByLanguage() {
        return Arrays.stream(QuestionType.values())
                .map(type -> {
                    Map<String, String> jsonMap = new HashMap<>();
                    jsonMap.put("key", type.getKey());
                    jsonMap.put("value", DpCommonUtil.isCurrentUserLanguageIsEnglish() ? type.getEn() : type.getAr());
                    return jsonMap;
                })
                .collect(Collectors.toList());
    }

    public static QuestionType fromString(String text) {
        return Arrays.stream(QuestionType.values())
                .filter(type ->
                        type.getEn().equalsIgnoreCase(text) || type.getAr().equalsIgnoreCase(text) ||
                                type.name().equalsIgnoreCase(text) || type.getKey().equalsIgnoreCase(text)
                )
                .findFirst()
                .orElseThrow(() -> new CustomDataException(
                        String.format(
                                LocalizedErrorCodes.WRONG_STATUS_ERROR_MESSAGES.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), getAllByLanguage()),
                        LocalizedErrorCodes.WRONG_STATUS_ERROR_MESSAGES.getErrorCode()
                ));
    }
}
