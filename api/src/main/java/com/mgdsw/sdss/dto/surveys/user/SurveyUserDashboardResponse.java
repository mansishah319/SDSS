/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.user;

import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 01 Jul 2025
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveyUserDashboardResponse implements BaseMarkerDTO {

    private Boolean internalUser;
    private Boolean delphiModerator;

    // User Information
    private Long employeeGrpNumber;
    private String employeeFullName;
    private String departmentName;
    private String jobTitle;

    // Survey Statistics
    private long totalSurveys;
    private long answeredSurveys;
    private long pendingSurveys;
    private double participationRate;
    private long recentActivity;
    private LocalDateTime lastActivityDate;

    // Survey Counts
    private long activeSurveysCount;
    private long pendingSurveysCount;
    private long completedSurveysCount;
    private long totalAnswersCount;

    // Delphi moderator review statistics
    private long totalReviewsMade;
    private long reviewedSolutionsCount;
    private long reviewedChallengesCount;

    // Survey Lists
    private List<SurveyUserActiveResponse> activeSurveysList;
    private List<SurveyUserActiveResponse> pendingSurveysList;
    private List<SurveyUserActiveResponse> completedSurveysList;
}
