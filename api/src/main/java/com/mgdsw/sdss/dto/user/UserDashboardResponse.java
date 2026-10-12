/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.roles.RolesResponse;
import com.mgdsw.sdss.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
public class UserDashboardResponse extends BaseResponse<Integer> {

    private String fullName;
    private String grpNumber;
    private String language;
    private String middleName;
    private Status status;
    private String mobile;
    private String alternativeMobile;
    private String email;
    private String alternativeEmail;
    private String type;
    private String title;
    private List<RolesResponse> roles;

    @JsonProperty("statusLocal")
    public String getLocalStatus() {
        return DpCommonUtil.retrieveLocalValue(status.getNameEn(), status.getNameAr());
    }
}
