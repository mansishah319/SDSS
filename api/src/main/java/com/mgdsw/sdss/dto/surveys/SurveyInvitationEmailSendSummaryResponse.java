/**
 * ***************************************************************
 * Copyright ©1956-2026 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyInvitationEmailSendSummaryResponse {

    private Integer totalCount;
    private Integer successCount;
    private Integer failedCount;
    private Boolean allSent;
    private String message;
    private String reason;
    private List<SurveyInvitationEmailSendResultResponse> results;
}
