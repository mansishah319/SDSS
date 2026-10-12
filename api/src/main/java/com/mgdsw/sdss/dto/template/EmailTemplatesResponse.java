/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.template.type.TemplateTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
public class EmailTemplatesResponse extends BaseResponse<Integer> {

    private String title;
    private String subjectAr;
    private String subjectEn;
    private String contentAr;
    private String contentEn;
    private Boolean isDefault;

    private TemplateTypeResponse templateType;

    @JsonProperty("localContent")
    public String getLocalContent() {
        return DpCommonUtil.retrieveLocalValue(contentEn, contentAr);
    }

    @JsonProperty("localSubject")
    public String getLocalSubject() {
        return DpCommonUtil.retrieveLocalValue(subjectEn, subjectAr);
    }
}
