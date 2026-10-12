/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.surveys;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.enums.SdssStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Repository
public interface SdssSurveyRoundRepository extends BaseRepository<SdssSurveyRound, Integer> {

    Optional<SdssSurveyRound> findByRoundNumberAndSurvey(Integer roundNumber, SdssSurvey survey);

    @Transactional
    @Modifying
    @Query("DELETE FROM SdssSurveyRound cr WHERE cr.survey = :survey")
    void deleteBySurvey(@Param("survey") SdssSurvey survey);

    boolean existsByRoundNumberAndSurvey(Integer roundNumber, SdssSurvey survey);

    List<SdssSurveyRound> findBySurvey(SdssSurvey survey);

    Optional<SdssSurveyRound> findBySurveyAndActiveTrue(SdssSurvey survey);

    List<SdssSurveyRound> findBySurveyAndStatus(SdssSurvey survey, SdssStatus status);

    List<SdssSurveyRound> findByStatusAndCreatedBy(SdssStatus status, String createdBy);

    @Query("SELECT sr FROM SdssSurveyRound sr " +
            "WHERE sr.status IN :statuses " +
            "AND sr.createdBy = :createdBy")
    List<SdssSurveyRound> findByStatusesAndCreatedBy(@Param("statuses") List<SdssStatus> statuses, @Param("createdBy") String createdBy);
}
