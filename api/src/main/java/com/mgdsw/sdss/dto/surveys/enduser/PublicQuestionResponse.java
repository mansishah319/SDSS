/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys.enduser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicQuestionResponse implements BaseMarkerDTO {

    private Integer id;
    @JsonIgnore
    private QuestionType questionType;

    private String titleEn;
    private String titleAr;
    private String descriptionEn;
    private String descriptionAr;
    private Boolean showPublicQuestions;

    private List<PublicOptionsResponse> options;

    @JsonProperty("localTitle")
    public String getLocalTitle() {
        return DpCommonUtil.retrievePublicLocalValue(titleEn, titleAr);
    }

    @JsonProperty("localDescription")
    public String getLocalDescription() {
        return DpCommonUtil.retrievePublicLocalValue(descriptionEn, descriptionAr);
    }

    @JsonProperty("questionTypeKey")
    public String questionTypeKey() {
        return questionType.getKey();
    }

    @JsonProperty("localQuestionType")
    public String localQuestionType() {
        return DpCommonUtil.retrievePublicLocalValue(questionType.getEn(), questionType.getAr());
    }
}
