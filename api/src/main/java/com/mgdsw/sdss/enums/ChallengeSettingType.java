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
public enum ChallengeSettingType {

    TOPIC_CLASSIFICATION("Topic Classification", "موضوع التصنيف"),
    LEVEL_OF_DECISION("Level of Decision", "مستوى القرار"),
    IMPORTANCE_OF_DECISION("Importance of Decision", "أهمية القرار"),
    IMPACT("Impact", "تأثير الموضوع"),
    FIELD("Field", "مجال الموضوع");

    private final String en;
    private final String ar;
}
