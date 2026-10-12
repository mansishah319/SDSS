/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.dto.surveys.enduser.PublicOptionsResponse;
import com.mgdsw.sdss.dto.surveys.enduser.PublicQuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 28 Nov 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SdssSurveyUserQuestionAnswersResponse extends BaseResponse<Integer> {

    private Double probabilityOfOccurrence;
    private Double impactSeverity;
    private String responseText;
    private Boolean correct;

    private PublicQuestionResponse question;
    private PublicCurrentInvitedSurveyUserResponse user;
    private List<PublicOptionsResponse> selectedOptions;
}
