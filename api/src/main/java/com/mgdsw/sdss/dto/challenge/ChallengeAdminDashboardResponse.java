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
import com.mgdsw.sdss.enums.ChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.DateFormat.DATETIME_PATTERN;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn May 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeAdminDashboardResponse extends BaseResponse<Integer> {

    private String titleAr;
    private String titleEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
    private LocalDateTime endTime;

    private String descriptionAr;
    private String descriptionEn;

    private String topicClassification;
    private String levelOfDecision;
    private String importanceOfDecision;
    private String impact;
    private String field;

    private int sectorId;
    private String sectorName;

    private String departmentId;
    private String departmentName;
    private String localChallengeStatus;
    private ChallengeStatus status;

    private List<SolutionResponse> finalDecisions;

    @JsonProperty("localTitle")
    public String getLocalTitle() {
        return DpCommonUtil.retrieveLocalValue(titleEn, titleAr);
    }

    @JsonProperty("localDescription")
    public String getLocalDescription() {
        return DpCommonUtil.retrieveLocalValue(descriptionEn, descriptionAr);
    }

    @JsonProperty("statusLocal")
    public String getStatusLocal() {
        return DpCommonUtil.retrieveLocalValue(status.getEnglishName(), status.getArabicName());
    }
}
