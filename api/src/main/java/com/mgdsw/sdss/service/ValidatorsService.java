/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.challenge.ChallengeAdminDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeForUserDashboardResponse;
import com.mgdsw.sdss.dto.invitation.InvitationResponse;
import com.mgdsw.sdss.dto.user.ChallengeUsersResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.Validators;
import com.mgdsw.sdss.enums.ChallengeStatus;
import com.mgdsw.sdss.enums.UserInvitationStatus;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface ValidatorsService extends BaseService<Integer, Validators> {

    void create(Challenges challenges, List<User> userList);

    void sendPendingChallengeValidatorInvitations(Integer challengeId);

    List<User> findAllInvitedValidatorUsersByChallenges(Challenges challenges);

    List<ChallengeUsersResponse> retrieveValidatorUsersByChallenge(Challenges challenges);

    long countAllInvitationsForTheCurrentValidatorUser();

    long countAllInvitationsForTheCurrentValidatorUserBasedOnStatus(UserInvitationStatus status);

    List<ChallengeForUserDashboardResponse> retrieveChallengesForTheCurrentValidatorUserBasedOnInvitationStatus(User user, UserInvitationStatus status);

    List<ChallengeAdminDashboardResponse> retrieveChallengesForTheCurrentValidatorUserBasedOnChallengeStatus(User user, ChallengeStatus challengeStatus);

    InvitationResponse updateInvitationStatus(Integer challengeId, String invitationStatus, String reason);

    void removeAllByChallenge(Challenges challengeNeedToRemove);

    InvitationResponse retrieveTheCurrentValidatorInvitation(Challenges challenge);
}
