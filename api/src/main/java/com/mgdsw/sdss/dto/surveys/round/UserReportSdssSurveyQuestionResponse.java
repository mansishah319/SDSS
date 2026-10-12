/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.round;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.lookups.LookupInfoResponse;
import com.mgdsw.sdss.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 18 Feb 2025
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserReportSdssSurveyQuestionResponse extends LookupInfoResponse<Integer> {

    @JsonIgnore
    private QuestionType questionType;
    private Boolean showPublicQuestions;

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
