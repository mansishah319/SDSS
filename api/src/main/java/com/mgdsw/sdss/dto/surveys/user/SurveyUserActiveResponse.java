/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.user;

import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 01 Jul 2025
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveyUserActiveResponse implements BaseMarkerDTO {

    private Integer surveyId;
    private String surveyNameEn;
    private String surveyNameAr;
    private String descriptionEn;
    private String descriptionAr;
    private Integer totalRounds;
    private long activeRounds;
    private boolean hasAnswered;
    private String status;
}

