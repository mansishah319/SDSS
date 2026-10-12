/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.enums;

import com.mgdsw.sdss.common.utils.DpCommonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Getter
@AllArgsConstructor
public enum ChallengeStatus {

    COMPLETED("Completed", "مكتمل"),
    OPEN("Open", "مفتوح"),
    CLOSED("Closed", "مغلق"),
    VOTING("Voting", "التصويت"),
    REVISED("Revised", "تم مراجعته"),
    PAUSED("Paused", "الإيقاف المؤقت"),
    UNDER_VALIDATOR_REVIEW("Under Validator Review", "تحت مراجعة المدقق"),
    UNDER_ADMIN_REVIEW("Under Admin Review", "تحت مراجعة الأدمن"),
    NEW("New", "جديد"),
    URGENT("Urgent", "ضروري");

    private final String englishName;
    private final String arabicName;

    public static ChallengeStatus fromString(String text) {
        return Arrays.stream(ChallengeStatus.values())
                .filter(type -> type.getEnglishName().equalsIgnoreCase(text) || type.getArabicName().equalsIgnoreCase(text) || type.name().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }

    public static List<String> getAllByLanguage() {
        return Arrays.stream(ChallengeStatus.values())
                .map(type -> DpCommonUtil.isCurrentUserLanguageIsEnglish() ? type.getEnglishName() : type.getArabicName())
                .collect(Collectors.toList());
    }

    public static List<String> getChallengeStatusByLanguage(Language lang) {
        return Arrays.stream(ChallengeStatus.values())
                .map(type -> lang.equals(Language.EN) ? type.getEnglishName() : type.getArabicName())
                .collect(Collectors.toList());
    }
}
