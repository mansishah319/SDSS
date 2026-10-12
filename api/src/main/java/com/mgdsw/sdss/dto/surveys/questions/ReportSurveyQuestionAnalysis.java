/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.questions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportSurveyQuestionAnalysis implements BaseMarkerDTO {

    @Builder.Default
    private Map<String, Long> userQualificationSpecificationAnalysis = new HashMap<>();
    @Builder.Default
    private Map<String, Long> userNationalityAnalysis = new HashMap<>();
    @Builder.Default
    private Map<String, Long> userMaritalStatusAnalysis = new HashMap<>();
    @Builder.Default
    private Map<String, Long> userJobAnalysis = new HashMap<>();

    @Builder.Default
    private Map<String, Long> userGenderAnalysis = new HashMap<>();
    @Builder.Default
    private Map<String, Long> userDepartmentAnalysis = new HashMap<>();
}
