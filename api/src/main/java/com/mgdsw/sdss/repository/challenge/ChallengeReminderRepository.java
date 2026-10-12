/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.challenge;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.challenge.ChallengeReminder;
import com.mgdsw.sdss.entity.challenge.Challenges;
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
public interface ChallengeReminderRepository extends BaseRepository<ChallengeReminder, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ChallengeReminder cr WHERE cr.challenges = :challenges")
    void deleteByChallenges(@Param("challenges") Challenges challenges);

    List<ChallengeReminder> findByChallenges(Challenges challenges);
}
