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
import com.mgdsw.sdss.entity.survey.Survey;
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
public interface SurveyRepository extends BaseRepository<Survey, Integer> {

    Optional<Survey> findBySolutionAndUser(Solution solution, User user);

    List<Survey> findBySolution(Solution solution);

    @Query("SELECT s.solution.id AS solutionId, AVG(s.rating) AS averageRating, COUNT(s.id) AS ratingCount " +
            "FROM Survey s WHERE s.solution.id IN :solutionIds GROUP BY s.solution.id")
    List<SolutionRatingProjection> aggregateRatingsBySolutionIds(@Param("solutionIds") Collection<Integer> solutionIds);
}
