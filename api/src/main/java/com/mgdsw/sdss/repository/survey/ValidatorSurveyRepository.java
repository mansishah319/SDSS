/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.survey;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.entity.survey.ValidatorSurvey;
import com.mgdsw.sdss.entity.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface ValidatorSurveyRepository extends BaseRepository<ValidatorSurvey, Integer> {

    Optional<ValidatorSurvey> findBySolutionAndUser(Solution solution, User user);

    List<ValidatorSurvey> findBySolution(Solution solution);

    long countAllByUser(User user);

    @Query("SELECT COUNT(DISTINCT vs.solution.id) FROM ValidatorSurvey vs WHERE vs.user = :user")
    long countDistinctSolutionsByUser(@Param("user") User user);

    @Query("SELECT COUNT(DISTINCT vs.challenges.id) FROM ValidatorSurvey vs WHERE vs.user = :user")
    long countDistinctChallengesByUser(@Param("user") User user);

    @Query("SELECT vs.solution.id AS solutionId, AVG(vs.rating) AS averageRating, COUNT(vs.id) AS ratingCount " +
            "FROM ValidatorSurvey vs WHERE vs.solution.id IN :solutionIds GROUP BY vs.solution.id")
    List<SolutionRatingProjection> aggregateRatingsBySolutionIds(@Param("solutionIds") Collection<Integer> solutionIds);
}
