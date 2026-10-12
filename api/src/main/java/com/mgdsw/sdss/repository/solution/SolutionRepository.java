/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.solution;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.solution.Solution;
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
public interface SolutionRepository extends BaseRepository<Solution, Integer> {

    List<Solution> findAllByChallenges(Challenges challenges);

    List<Solution> findAllByChallengesAndQueLevel(Challenges challenges, Integer roundLevel);

    @Transactional
    @Modifying
    @Query("UPDATE Solution s SET s.queLevel = :newQueLevel WHERE s.id IN :solutionIds")
    void updateQueLevelForSolutions(List<Integer> solutionIds, int newQueLevel);

    @Transactional
    @Modifying
    @Query("UPDATE Solution s SET s.queLevel = :newQueLevel, s.isFinal = :isFinal WHERE s.id IN :solutionIds")
    void updateQueLevelAndIsFinalForSolutions(List<Integer> solutionIds, Boolean isFinal, Integer newQueLevel);

    List<Solution> findAllByChallengesAndIsFinal(Challenges challenges, Boolean isFinal);

    @Transactional
    @Modifying
    @Query("DELETE FROM Solution cr WHERE cr.challenges = :challenges")
    void deleteByChallenges(@Param("challenges") Challenges challenges);

    @Query("SELECT v.queLevel FROM Voting v WHERE v.challenges = :challenges")
    List<Integer> findQueLevelByChallenges(@Param("challenges") Challenges challenges);

    @Query("SELECT s FROM Solution s WHERE s.challenges = :challenges AND LOCATE(CONCAT(',', :stageLevel, ','), CONCAT(',', s.stageList, ',')) > 0")
    List<Solution> findByChallengesAndStageLevel(@Param("challenges") Challenges challenges, @Param("stageLevel") Integer stageLevel);

    @Query("SELECT DISTINCT s.user.id FROM Solution s WHERE s.id IN :solutionIds")
    List<Integer> findUserIdsBySolutionIds(@Param("solutionIds") List<Integer> solutionIds);
}
