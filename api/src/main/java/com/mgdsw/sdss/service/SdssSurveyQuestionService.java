/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionRequest;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.questions.UpdateSurveyQuestionRequest;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.
 * @createdOn Oct, 2024
 */
public interface SdssSurveyQuestionService extends BaseService<Integer, SdssSurveyQuestion> {

    SdssSurveyQuestionResponse createNewSurveyQuestion(SdssSurveyQuestionRequest questionRequest, SdssSurvey survey, SdssSurveyRound surveyRound);

    List<SdssSurveyQuestion> findByRound(SdssSurveyRound round);

    SdssSurveyQuestionResponse updateSurveyQuestion(UpdateSurveyQuestionRequest updateRequest, Integer questionId);
}
