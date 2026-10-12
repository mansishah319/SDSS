/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.round;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.ReportSdssSurveyResponse;
import com.mgdsw.sdss.dto.surveys.lookups.LookupInfoResponse;
import com.mgdsw.sdss.enums.SdssStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.mgdsw.sdss.common.utils.DpDateUtil.DATE_TIME_FORMAT;

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
public class RoundReportMainResponse extends LookupInfoResponse<Integer> {

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime startTime;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime endTime;
    private Boolean manualHandle;
    private SdssStatus status;
    private int roundNumber;
    private Boolean active;
    private ReportSdssSurveyResponse survey;

    @JsonProperty("localStatus")
    public String getLocalStatus() {
        return DpCommonUtil.retrieveLocalValue(status.getEn(), status.getAr());
    }

    @JsonProperty("currentUserIsTheSurveyModerator")
    public Boolean currentUserIsTheSurveyModerator() {
        return DpCommonUtil.retrieveCurrentUserGrpNumber().equals(Long.valueOf(super.getCreatedBy()));
    }

    @JsonProperty("isAllowedToUpdateOrDelete")
    public Boolean isAllowedToUpdateOrDeleteThisSurveyRound() {
        return status.equals(SdssStatus.PENDING);
    }
}