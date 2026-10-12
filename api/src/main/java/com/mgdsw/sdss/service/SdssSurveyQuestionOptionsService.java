/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsRequest;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.dto.surveys.questions.options.UpdateSurveyQuestionOptionsRequest;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestionOptions;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.
 * @createdOn Oct, 2024
 */
public interface SdssSurveyQuestionOptionsService extends BaseService<Integer, SdssSurveyQuestionOptions> {

    SdssSurveyQuestionOptionsResponse createNewOptionForSpecificQuestion(SdssSurveyQuestion surveyQuestion, SdssSurveyQuestionOptionsRequest optionsRequest);

    void createNewListOfOptionsForSpecificQuestion(SdssSurveyQuestion surveyQuestion, List<SdssSurveyQuestionOptionsRequest> optionsRequestList);

    void updateListOfOptionsForSpecificQuestion(SdssSurveyQuestion updatedSurveyQuestion, List<UpdateSurveyQuestionOptionsRequest> optionsRequestList);

    SdssSurveyQuestionOptionsResponse updateOptionForSpecificQuestion(SdssSurveyQuestion surveyQuestion, UpdateSurveyQuestionOptionsRequest optionsRequest);
}
