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
 * @createdOn Jun, 2024
 */

@Getter
@AllArgsConstructor
public enum TemplatesTypeKeys {

    WELCOME_KEY("Welcome", "ترحيب"),

    SURVEY_INVITATION_KEY("Survey Invitation", "دعوة الإستبيان"),
    SURVEY_STAGE_DONE_KEY("Reminder Survey stage completed", "تذكير بالإنتهاء من مرحلة الإستبيان"),
    SURVEY_LAST_STAGE_DONE_KEY("Reminder Last Survey stage completed", "تذكير بالإنتهاء من مرحلة الإستبيان الأخيرة"),

    VALIDATOR_INVITATION_KEY("Validator Invitation", "دعوة الُمدقق"),
    CHALLENGE_USER_INVITATION_KEY("Challenge Invitation", "دعوة تحدي"),
    NEW_REGISTERED_USER_INVITATION_KEY("New Registered User Invitation", "دعوة مستخدم مسجل جديد"),
    VALIDATOR_CHALLENGE_NEW_KEY("Validate Challenge By Validator", "التحقق من التحدي بواسطة المدقق"),
    CHALLENGE_INVITATION_REMINDER_KEY("Challenge Invitation Reminder", "تحدي جديد تم تعيينه: يُرجى مراجعته"),

    VALIDATOR_NEW_VALIDATE_REMINDER_KEY("Validator under review challenge Assigned", "تعيين تحدي لمراجعة المُدقِّق"),
    COMMANDER_NEW_VALIDATE_REMINDER_KEY("Commander under review challenge Assigned", "تعيين تحدي لمراجعة المدير");

    private final String en;
    private final String ar;

    public static TemplatesTypeKeys fromString(String text) {
        return Arrays.stream(TemplatesTypeKeys.values())
                .filter(type -> type.getEn().equalsIgnoreCase(text) || type.getAr().equalsIgnoreCase(text) || type.name().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new CustomDataException(
                        String.format(
                                LocalizedErrorCodes.WRONG_STATUS_ERROR_MESSAGES.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), getAllByLanguage()),
                        LocalizedErrorCodes.WRONG_STATUS_ERROR_MESSAGES.getErrorCode()
                ));
    }

    public static List<String> getAllByLanguage() {
        return Arrays.stream(TemplatesTypeKeys.values())
                .map(type -> DpCommonUtil.isCurrentUserLanguageIsEnglish() ? type.getEn() : type.getAr())
                .collect(Collectors.toList());
    }

    public static List<String> getTemplatesTypeKeysByLanguage(Language lang) {
        return Arrays.stream(TemplatesTypeKeys.values())
                .map(type -> lang.equals(Language.EN) ? type.getEn() : type.getAr())
                .collect(Collectors.toList());
    }
}

