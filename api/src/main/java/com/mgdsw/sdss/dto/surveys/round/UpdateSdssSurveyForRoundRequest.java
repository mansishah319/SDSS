/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys.round;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_DESCRIPTION_TITLE_AR;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_DESCRIPTION_TITLE_EN;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_TITLE_AR;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_TITLE_EN;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSdssSurveyForRoundRequest {

    @Builder.Default
    private String titleEn = ROUND_DEFAULT_TITLE_EN;
    @Builder.Default
    private String titleAr = ROUND_DEFAULT_TITLE_AR;

    @Builder.Default
    private String descriptionEn = ROUND_DEFAULT_DESCRIPTION_TITLE_EN;
    @Builder.Default
    private String descriptionAr = ROUND_DEFAULT_DESCRIPTION_TITLE_AR;

    @Builder.Default
    private Boolean manualHandle = Boolean.TRUE;

    private String startTime;
    private String endTime;

    private int roundNumber;

    public String getTitleEn() {
        return titleEn != null ? titleEn : ROUND_DEFAULT_TITLE_EN;
    }

    public String getTitleAr() {
        return titleAr != null ? titleAr : ROUND_DEFAULT_TITLE_AR;
    }

    public String getDescriptionEn() {
        return descriptionEn != null ? descriptionEn : ROUND_DEFAULT_DESCRIPTION_TITLE_EN;
    }

    public String getDescriptionAr() {
        return descriptionAr != null ? descriptionAr : ROUND_DEFAULT_DESCRIPTION_TITLE_AR;
    }
}
