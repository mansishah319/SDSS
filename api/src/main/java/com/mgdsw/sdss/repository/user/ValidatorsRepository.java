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
import com.mgdsw.sdss.entity.user.Validators;
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
public interface ValidatorsRepository extends BaseRepository<Validators, Integer> {

    List<Validators> findAllByChallenges(Challenges challenges);

    List<Validators> findByChallenges(Challenges challenges);

    long countAllByUser(User user);

    long countAllByUserAndStatus(User user, UserInvitationStatus status);

    List<Validators> findByChallengesAndUser(Challenges challenges, User user);

    List<Validators> findAllByUser(User user);

    List<Validators> findByUserAndStatus(User user, UserInvitationStatus status);

    @Transactional
    @Modifying
    @Query("DELETE FROM Validators cr WHERE cr.challenges = :challenges")
    void deleteByChallenges(@Param("challenges") Challenges challenges);
}
