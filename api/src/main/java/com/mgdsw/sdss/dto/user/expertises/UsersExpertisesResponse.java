/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.user.expertises;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsersExpertisesResponse implements BaseMarkerDTO {

    private Integer userId;
    private Integer expertisesId;
    private String expertisesName;
    private String expertLevel;
    private Boolean isPrimary;

    @JsonProperty("isPrimaryLocal")
    public String isPrimaryLocal() {
        return Boolean.TRUE.equals(isPrimary) ?
                DpCommonUtil.retrieveLocalValue("Yes", "نعم") :
                DpCommonUtil.retrieveLocalValue("No", "لا");
    }
}
