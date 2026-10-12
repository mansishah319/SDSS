/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.variety;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.variety.Voting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface VotingRepository extends BaseRepository<Voting, Integer> {

    List<Voting> findBySolution(Solution solution);

    List<Voting> findByChallenges(Challenges challenges);

    List<Voting> findByChallengesAndQueLevel(Challenges challenges, Integer queLevel);

    @Query("SELECT v.queLevel FROM Voting v WHERE v.challenges = :challenges")
    List<Integer> findQueLevelByChallenges(@Param("challenges") Challenges challenges);

    boolean existsBySolutionAndUser(Solution solution, User user);

    Long deleteBySolutionAndUser(Solution solution, User user);

    long countBySolution(Solution solution);

    long countByChallengesIn(List<Challenges> challenges);

    void deleteAllByChallenges(Challenges challenges);

    List<Voting> findByChallengesAndSolution(Challenges challenge, Solution solution);
}
