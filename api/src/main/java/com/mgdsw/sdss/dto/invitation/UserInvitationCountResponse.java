/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.invitation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.dto.user.ChallengeUsersResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Feb, 2024
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInvitationCountResponse implements BaseMarkerDTO {

    private long countOfInvitationUsers;
    private long countOfInvitationRejected;
    private long countOfInvitationAccepted;
    private long countOfInvitationPending;
    private List<ChallengeUsersResponse> invitedInternalUserResponse;
    private List<ChallengeUsersResponse> pendingUsersResponse;
    private List<ChallengeUsersResponse> rejectedUsersResponse;
    private List<ChallengeUsersResponse> acceptedUsersResponse;
}
