/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.user;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.UserInvitation;
import com.mgdsw.sdss.enums.UserInvitationStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface UserInvitationRepository extends BaseRepository<UserInvitation, Integer> {

    List<UserInvitation> findAllByChallenges(Challenges challenges);

    List<UserInvitation> findAllByChallengesAndStatus(Challenges challenges, UserInvitationStatus status);

    long countAllByChallengesAndStatus(Challenges challenges, UserInvitationStatus status);

    long countAllByChallenges(Challenges challenges);

    long countAllByUser(User user);

    long countAllByUserAndStatus(User user, UserInvitationStatus status);

    List<UserInvitation> findByChallengesAndUser(Challenges challenges, User user);

    List<UserInvitation> findAllByUser(User user);

    List<UserInvitation> findByUserAndStatus(User user, UserInvitationStatus status);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserInvitation cr WHERE cr.challenges = :challenges")
    void deleteByChallenges(@Param("challenges") Challenges challenges);
}
