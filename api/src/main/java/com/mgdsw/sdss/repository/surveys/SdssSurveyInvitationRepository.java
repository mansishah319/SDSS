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
import com.mgdsw.sdss.entity.surveys.SdssSurveyInvitation;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Repository
public interface SdssSurveyInvitationRepository extends BaseRepository<SdssSurveyInvitation, Integer> {

    List<SdssSurveyInvitation> findBySurveyRoundAndInvitedUser(SdssSurveyRound surveyRound, SdssSurveyUsers invitedUser);

    List<SdssSurveyInvitation> findByInvitedUser(SdssSurveyUsers invitedUser);

    List<SdssSurveyInvitation> findBySurvey(SdssSurvey survey);

    List<SdssSurveyInvitation> findBySurveyAndInvitedUser(SdssSurvey survey, SdssSurveyUsers sdssSurveyUsers);

    @Query("SELECT COUNT(DISTINCT si.invitedUser) " +
            "FROM SdssSurveyInvitation si " +
            "WHERE si.survey = :survey AND si.surveyRound = :surveyRound")
    long countDistinctInvitedUsersBySurveyAndSurveyRound(@Param("survey") SdssSurvey survey, @Param("surveyRound") SdssSurveyRound surveyRound);
}
