/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.lookup.user;

import com.mgdsw.sdss.dto.challenge.ChallengeAdminDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeForUserDashboardResponse;
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
public class TheValidatorsDashboardResponse {

    private long totalInvitationsCount;
    private long totalAcceptedCount;
    private long totalPendingCount;
    private long totalRejectedCount;

    private List<ChallengeForUserDashboardResponse> pendingInvitationList;
    private List<ChallengeForUserDashboardResponse> acceptedInvitationList;
    private List<ChallengeForUserDashboardResponse> rejectedInvitationsList;
    private List<ChallengeAdminDashboardResponse> completedInvitationsList;

    private List<ChallengeAdminDashboardResponse> requestsUnderReview;
    private List<ChallengeAdminDashboardResponse> reviewedChallengesList;
    private List<ChallengeAdminDashboardResponse> closedChallengesList;
}
