/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.challenge.ChallengeForUserDashboardResponse;
import com.mgdsw.sdss.dto.invitation.InvitationResponse;
import com.mgdsw.sdss.dto.invitation.UserInvitationCountResponse;
import com.mgdsw.sdss.dto.user.ChallengeUsersResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.UserInvitation;
import com.mgdsw.sdss.enums.ChallengeStatus;
import com.mgdsw.sdss.enums.UserInvitationStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface UserInvitationService extends BaseService<Integer, UserInvitation> {

    void createUserInvitationChallengeInternal(User user, Challenges challenges, List<LocalDateTime> reminders);

    void sendPendingChallengeInvitations(Integer challengeId, List<String> reminderDateTimes);

    InvitationResponse createIndividualInvitation(String email, Integer challengeId, HttpServletRequest httpServletRequest);

    List<ChallengeUsersResponse> findAllInvitedUsersByChallenges(Challenges challenges);

    UserInvitationCountResponse retrieveCountResultForInvitedInternalUsers(Challenges challenges);

    InvitationResponse updateInvitationStatus(Integer challengeId, String invitationStatus, String reason);

    long countAllInvitationsForTheCurrentUser();

    long countAllInvitationsForTheCurrentUserBasedOnStatus(UserInvitationStatus status);

    List<Challenges> findChallengesByUser(User user);

    List<ChallengeForUserDashboardResponse> retrieveChallengesForTheCurrentUserBasedOnInvitationStatus(User user, UserInvitationStatus status);

    List<ChallengeForUserDashboardResponse> retrieveChallengesForTheCurrentUserBasedOnChallengeStatus(User user, ChallengeStatus challengeStatus);

    void removeAllByChallenge(Challenges challengeNeedToRemove);

    List<InvitationResponse> createInvitationWhileSession(List<Long> userGrpList, Integer challengeId);

    InvitationResponse retrieveTheCurrentUserInvitation(Challenges challenge);
}
