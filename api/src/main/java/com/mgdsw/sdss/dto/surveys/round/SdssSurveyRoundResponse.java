/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys.round;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.lookups.LookupInfoResponse;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionResponse;
import com.mgdsw.sdss.enums.SdssStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.mgdsw.sdss.common.utils.DpDateUtil.DATE_TIME_FORMAT;

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
public class SdssSurveyRoundResponse extends LookupInfoResponse<Integer> {

    private Boolean manualHandle;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime startTime;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime endTime;
    private int roundNumber;
    private SdssStatus status;
    private Boolean active;
    private List<SdssSurveyQuestionResponse> questions;

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
