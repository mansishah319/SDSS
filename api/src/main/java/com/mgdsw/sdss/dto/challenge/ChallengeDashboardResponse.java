/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.challenge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.solution.SolutionResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import com.mgdsw.sdss.enums.ChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.mgdsw.sdss.common.utils.DpDateUtil.DATE_TIME_FORMAT;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Feb, 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeDashboardResponse extends BaseResponse<Integer> {

    private int isFinal;
    private int queLevel;
    private ChallengeStatus status;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private String startTime;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private String endTime;
    private String challengeTitle;
    private String challengeDetails;
    private List<String> youtubeUrls;
    private List<SolutionResponse> challengeSolutions;
    private UserDropDownListResponse moderator;
    private UserDropDownListResponse assistantModerator;

    @JsonProperty("statusLocal")
    public String getLocalStatus() {
        return DpCommonUtil.retrieveLocalValue(status.getEnglishName(), status.getArabicName());
    }
}
