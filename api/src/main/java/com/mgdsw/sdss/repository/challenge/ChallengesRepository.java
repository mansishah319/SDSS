/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.challenge;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.strategic.FutureStrategicDirection;
import com.mgdsw.sdss.entity.strategic.FutureStrategicGoals;
import com.mgdsw.sdss.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface ChallengesRepository extends BaseRepository<Challenges, Integer>, RevisionRepository<Challenges, Integer, Integer> {

    List<Challenges> findAllByStatus(ChallengeStatus status);

    long countByStatus(ChallengeStatus status);

    @Query("SELECT COUNT(c) FROM Challenges c WHERE c.departmentId = :departmentId")
    Long countByDepartmentId(@Param("departmentId") String departmentId);

    long countByFutureStrategicGoals(FutureStrategicGoals futureStrategicGoals);

    List<Challenges> findByFutureStrategicGoals(FutureStrategicGoals futureStrategicGoals);

    long countByFutureStrategicDirection(FutureStrategicDirection futureStrategicDirection);

    List<Challenges> findByFutureStrategicDirection(FutureStrategicDirection futureStrategicDirection);
}
