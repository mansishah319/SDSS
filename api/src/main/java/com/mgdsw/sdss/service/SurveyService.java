/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.survey.SurveyCreationRequest;
import com.mgdsw.sdss.dto.survey.SurveyForSolutionResponse;
import com.mgdsw.sdss.entity.survey.Survey;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface SurveyService extends BaseService<Integer, Survey> {

    SurveyForSolutionResponse createAndUpdate(SurveyCreationRequest request);

    SurveyForSolutionResponse update(SurveyCreationRequest request, Integer surveyId);

    List<SurveyForSolutionResponse> findAllBySolutionId(Integer solutionId);

    SurveyForSolutionResponse findSurveyBySolutionId(Integer solutionId);
}
