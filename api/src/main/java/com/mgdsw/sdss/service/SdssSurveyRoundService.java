/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.surveys.UpdateSdssSurveyStatus;
import com.mgdsw.sdss.dto.surveys.round.RoundReportMainResponse;
import com.mgdsw.sdss.dto.surveys.round.SdssSurveyRoundRequest;
import com.mgdsw.sdss.dto.surveys.round.SdssSurveyRoundResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.enums.SdssStatus;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.
 * @createdOn Oct, 2024
 */
public interface SdssSurveyRoundService extends BaseService<Integer, SdssSurveyRound> {

    SdssSurveyRoundResponse create(SdssSurveyRoundRequest request, SdssSurvey survey);

    SdssSurveyRoundResponse update(SdssSurveyRoundRequest request, SdssSurvey survey, Integer roundId);

    List<SdssSurveyRound> createList(List<SdssSurveyRoundRequest> roundRequestList, SdssSurvey survey);

    SdssSurveyRound updateStatusBySdssSurvey(UpdateSdssSurveyStatus updateSdssSurveyStatus, SdssSurvey sdssSurvey);

    SdssSurveyRound findByRoundNumberAndSurvey(Integer roundNumber, SdssSurvey survey);

    void deleteAllBySurvey(SdssSurvey survey);

    List<SdssSurveyRound> findBySurvey(SdssSurvey survey);

    SdssSurveyRound findActiveRound(SdssSurvey survey);

    List<SdssSurveyRound> findCompletedRound(SdssSurvey survey);

    List<RoundReportMainResponse> findAllRoundsBasedOnStatus(List<SdssStatus> statusList);

    void checkIfReportAllowedOrNot(SdssSurveyRound sdssSurveyRound);
}
