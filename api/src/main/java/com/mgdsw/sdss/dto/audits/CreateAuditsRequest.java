/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.audits;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Data
@Builder
public class CreateAuditsRequest {

    private String eventName;
    @JsonProperty("from_date")
    private String fromDate;
    @JsonProperty("to_date")
    private String toDate;

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
