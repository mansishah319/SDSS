/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys.lookups;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LookupInfoResponse<I extends Serializable> extends BaseResponse<I> {

    private String titleEn;
    private String titleAr;
    private String descriptionEn;
    private String descriptionAr;

    @JsonProperty("localTitle")
    public String getLocalTitle() {
        return DpCommonUtil.retrieveLocalValue(titleEn, titleAr);
    }

    @JsonProperty("localDescription")
    public String getLocalDescription() {
        return DpCommonUtil.retrieveLocalValue(descriptionEn, descriptionAr);
    }
}
