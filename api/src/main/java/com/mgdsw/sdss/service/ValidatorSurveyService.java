/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.survey.validator.ValidatorSurveyCreationRequest;
import com.mgdsw.sdss.dto.survey.validator.ValidatorSurveySolutionResponse;
import com.mgdsw.sdss.entity.survey.ValidatorSurvey;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface ValidatorSurveyService extends BaseService<Integer, ValidatorSurvey> {

    ValidatorSurveySolutionResponse create(ValidatorSurveyCreationRequest validatorSurveyCreationRequest);

    ValidatorSurveySolutionResponse update(ValidatorSurveyCreationRequest request, Integer surveyId);

    List<ValidatorSurveySolutionResponse> findAllBySolutionId(Integer solutionId);

    ValidatorSurveySolutionResponse findSurveyBySolutionId(Integer solutionId);
}
