/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.challenge;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "The Question response object for Question responses.")
public class ChallengesMajorDetailsResponse extends BaseResponse<Integer> {

    private String status;
    private String surveyStatus;
    private String titleAr;
    private String titleEn;
    private String visibility;
    private String descriptionAr;
    private String descriptionEn;
    private boolean allowImmediateAccess;

    @JsonProperty("localDescription")
    public String getLocalDescription() {
        return DpCommonUtil.isCurrentUserLanguageIsEnglish() ? descriptionEn : descriptionAr;
    }

    @JsonProperty("localTitle")
    public String getLocalTitle() {
        return DpCommonUtil.isCurrentUserLanguageIsEnglish() ? titleEn : titleAr;
    }
}
