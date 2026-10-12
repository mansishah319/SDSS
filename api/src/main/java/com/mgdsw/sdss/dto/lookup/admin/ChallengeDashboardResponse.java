/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.lookup.admin;

import com.mgdsw.sdss.dto.challenge.ChallengeAdminDashboardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn May 2024
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDashboardResponse {

    private long totalChallenges;

    private long completedCount;
    private long openCount;
    private long closedCount;
    private long votingCount;
    private long revisedCount;
    private long pausedCount;
    private long underReviewCount;
    private long newCount;
    private long urgentCount;

    private long recentlyOpenedPercentage;

    private List<ChallengeAdminDashboardResponse> completedChallengesList;
    private List<ChallengeAdminDashboardResponse> openChallengesList;
    private List<ChallengeAdminDashboardResponse> closedChallengesList;
    private List<ChallengeAdminDashboardResponse> votingChallengesList;
    private List<ChallengeAdminDashboardResponse> revisedChallengesList;
    private List<ChallengeAdminDashboardResponse> pausedChallengesList;
    private List<ChallengeAdminDashboardResponse> underReviewChallengesList;
    private List<ChallengeAdminDashboardResponse> newChallengesList;
    private List<ChallengeAdminDashboardResponse> urgentChallengesList; // this is for Under Admin Review Status

    private List<ChallengeAdminDashboardResponse> challengesResponseList;
}
