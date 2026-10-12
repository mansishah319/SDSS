/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.challenge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * @createdOn Sep, 2023
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "The Question response object for Question responses.")
public class SimpleChallengesResponse extends BaseResponse<Integer> {

    private String status;
    private String surveyStatus;
    private String localTitle;
    private String titleAr;
    private String titleEn;
    private String visibility;
    private String localDescription;
    private String descriptionAr;
    private String descriptionEn;
    private Long moderatorGrpNumber;
    private boolean allowImmediateAccess;

    private String departmentId;
    private String departmentName;

    private String subDepartmentId;
    private String subDepartmentName;

    private String sectionId;
    private String sectionName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
    private LocalDateTime endTime;

    private String field;
    private ChallengeSettingsResponse fieldResponse;

    private String impact;
    private ChallengeSettingsResponse impactResponse;

    private String levelOfDecision;
    private ChallengeSettingsResponse levelOfDecisionResponse;

    private String topicClassification;
    private ChallengeSettingsResponse topicClassificationResponse;

    private String importanceOfDecision;
    private ChallengeSettingsResponse importanceOfDecisionResponse;

    private Integer isFinal;
    private int queLevel;

    private List<String> youtubeUrls;

    private LocalDateTime votingTime;

    private UserDropDownListResponse moderator;
    private UserDropDownListResponse assistantModerator;
}
