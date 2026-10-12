/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys.questions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.lookups.LookupInfoResponse;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.dto.surveys.round.UserSurveyAnswersReportResponse;
import com.mgdsw.sdss.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportSdssSurveyQuestionResponse extends LookupInfoResponse<Integer> {

    @JsonIgnore
    private QuestionType questionType;
    private Boolean showPublicQuestions;
    private Double impactSeverityTotalPercentage;
    private Map<String, Serializable> userAnswersStatistics = new HashMap<>();
    private Double probabilityOfOccurrenceTotalPercentage;
    private ReportSurveyQuestionAnalysis questionAnalysis;
    private List<SdssSurveyQuestionOptionsResponse> options;
    private List<UserSurveyAnswersReportResponse> usersSurveyAnswers;

    @JsonProperty("currentUserIsTheSurveyModerator")
    public Boolean currentUserIsTheSurveyModerator() {
        return DpCommonUtil.retrieveCurrentUserGrpNumber().equals(Long.valueOf(super.getCreatedBy()));
    }

    @JsonProperty("questionTypeKey")
    public String questionTypeKey() {
        return questionType.getKey();
    }

    @JsonProperty("localQuestionType")
    public String localQuestionType() {
        return DpCommonUtil.retrieveLocalValue(questionType.getEn(), questionType.getAr());
    }
}
