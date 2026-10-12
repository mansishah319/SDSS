/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.round;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.dto.surveys.user.ReportInvitedSurveyUserResponse;
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
 * @creationDate 18 Feb 2025
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSurveyAnswersReportResponse extends BaseResponse<Integer> {

    private Boolean correct;
    private String responseText;
    private Double impactSeverity;
    private Double probabilityOfOccurrence;

    private ReportInvitedSurveyUserResponse user;
    private UserReportSdssSurveyQuestionResponse question;
    private List<SdssSurveyQuestionOptionsResponse> selectedOptions;
}
