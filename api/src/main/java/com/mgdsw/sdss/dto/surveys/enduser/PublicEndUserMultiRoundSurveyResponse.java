/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.enduser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.user.PublicCurrentInvitedSurveyUserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 25 Nov 2024
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicEndUserMultiRoundSurveyResponse {

    private Integer id;
    private String titleEn;
    private String titleAr;
    private String descriptionEn;
    private String descriptionAr;
    private List<PublicRoundResponse> activeRoundResponseList;
    private PublicCurrentInvitedSurveyUserResponse invitedUserResponse;

    @JsonProperty("localTitle")
    public String getLocalTitle() {
        return DpCommonUtil.retrievePublicLocalValue(titleEn, titleAr);
    }

    @JsonProperty("localDescription")
    public String getLocalDescription() {
        return DpCommonUtil.retrievePublicLocalValue(descriptionEn, descriptionAr);
    }
}
