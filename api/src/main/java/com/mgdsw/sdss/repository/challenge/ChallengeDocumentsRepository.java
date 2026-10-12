/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.challenge;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.challenge.ChallengeDocuments;
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
public interface ChallengeDocumentsRepository extends BaseRepository<ChallengeDocuments, Integer> {

    @Modifying
    @Query("UPDATE ChallengeDocuments cd SET cd.challenges = :challenges WHERE cd.id IN :documentIds")
    void updateChallengesForDocuments(@Param("challenges") Challenges challenges, @Param("documentIds") List<Integer> documentIds);

    List<ChallengeDocuments> findByChallenges(Challenges challenges);

    @Transactional
    @Modifying
    @Query("DELETE FROM ChallengeDocuments cd WHERE cd.challenges = :challenge AND cd.id NOT IN :ids")
    void deleteAllByChallengesExceptIds(@Param("challenge") Challenges challenge, @Param("ids") List<Integer> ids);

    @Query("SELECT d FROM ChallengeDocuments d WHERE d.challenges.id = :challengeId OR :challengeId IS NULL")
    List<ChallengeDocuments> findDocumentsByChallengeId(@Param("challengeId") Integer challengeId);
}
