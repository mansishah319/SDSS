/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.audits;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "The Audits response object for all Audits responses.")
public class AuditsResponse extends BaseResponse<Integer> {

    @JsonProperty("user_type")
    private String userType;
    @JsonProperty("event_ar")
    private String eventAr;
    @JsonProperty("event_en")
    private String eventEn;
    @JsonProperty("auditable_id")
    private int auditableId;
    @JsonProperty("auditable_type")
    private String auditableType;
    @JsonProperty("old_values")
    private String oldValues;
    @JsonProperty("new_values")
    private String newValues;
    private String url;
    @JsonProperty("ip_address")
    private String ipAddress;
    @JsonProperty("user_agent")
    private String userAgent;
    private String tags;
}
