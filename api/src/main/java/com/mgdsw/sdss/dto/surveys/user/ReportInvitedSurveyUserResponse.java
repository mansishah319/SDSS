/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 26 Nov 2024
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportInvitedSurveyUserResponse implements BaseMarkerDTO {

    private Integer id;
    private Long employeeGrpNumber;
    @JsonIgnore
    private String employeeInfoKey;
    private EmployeeInfoKeyResponse employeeInfo;
}
