/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.strategic.goals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.strategic.directions.StrategicDirectionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StrategicGoalsResponse extends BaseResponse<Integer> {

    private String nameAr;
    private String nameEn;
    private String localName;
    private List<Integer> strategicDirectionIds;
    private List<StrategicDirectionResponse> strategicDirectionResponseList;

    @JsonProperty("name")
    public String getName() {
        return retrieveLocalName();
    }

    @JsonProperty("localName")
    public String getLocalName() {
        return retrieveLocalName();
    }

    private String retrieveLocalName() {
        return DpCommonUtil.isCurrentUserLanguageIsEnglish() ? nameEn : nameAr;
    }
}
