/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.questions.options;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 20 Dec 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OptionStatisticResponse extends BaseResponse<Integer> {

    private String optionEn;
    private String optionAr;
    private Long count;

    @JsonProperty("currentUserIsTheSurveyModerator")
    public Boolean currentUserIsTheSurveyModerator() {
        return DpCommonUtil.retrieveCurrentUserGrpNumber().equals(Long.valueOf(super.getCreatedBy()));
    }

    @JsonProperty("localOption")
    public String localOption() {
        return DpCommonUtil.retrieveLocalValue(optionEn, optionAr);
    }
}
