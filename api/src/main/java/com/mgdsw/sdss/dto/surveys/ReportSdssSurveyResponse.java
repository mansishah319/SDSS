/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.lookups.LookupInfoResponse;
import com.mgdsw.sdss.enums.SurveyMode;
import com.mgdsw.sdss.enums.SurveyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
public class ReportSdssSurveyResponse extends LookupInfoResponse<Integer> {

    private Boolean active;
    private SurveyStatus status;
    private Boolean manualHandle;
    private List<String> jobList;
    private Integer numberOfRounds;
    private List<Long> allowedGrpList;
    private List<String> departmentList;

    @JsonProperty("localStatus")
    public String getLocalStatus() {
        return DpCommonUtil.retrieveLocalValue(status.getEn(), status.getAr());
    }

    @JsonProperty("mode")
    public String getSurveyMode() {
        SurveyMode surveyMode = Boolean.TRUE.equals(manualHandle) ? SurveyMode.MANUAL : SurveyMode.AUTOMATIC;
        return DpCommonUtil.retrieveLocalValue(surveyMode.getEn(), surveyMode.getAr());
    }

    @JsonProperty("currentUserIsTheSurveyModerator")
    public Boolean currentUserIsTheSurveyModerator() {
        return DpCommonUtil.retrieveCurrentUserGrpNumber().equals(Long.valueOf(super.getCreatedBy()));
    }

    @JsonProperty("isAllowedToUpdateOrDelete")
    public Boolean isAllowedToUpdateOrDeleteThisSurvey() {
        return status.equals(SurveyStatus.NOT_STARTED);
    }
}
