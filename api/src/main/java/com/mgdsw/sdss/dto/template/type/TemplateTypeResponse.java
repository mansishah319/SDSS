/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.template.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.common.utils.ListActionsUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateTypeResponse extends BaseResponse<Integer> {

    private String titleEn;
    private String titleAr;
    private String templateTypeKey;
    private String mandatoryFieldsEn;
    private String mandatoryFieldsAr;

    @JsonProperty("localTitle")
    public String getLocalTitle() {
        return DpCommonUtil.retrieveLocalValue(titleEn, titleAr);
    }

    @JsonProperty("mandatoryFieldsListEn")
    public List<String> getMandatoryFieldsListEn() {
        return ListActionsUtil.convert(mandatoryFieldsEn);
    }

    @JsonProperty("mandatoryFieldsListAr")
    public List<String> getMandatoryFieldsListAr() {
        return ListActionsUtil.convert(mandatoryFieldsAr);
    }
}
