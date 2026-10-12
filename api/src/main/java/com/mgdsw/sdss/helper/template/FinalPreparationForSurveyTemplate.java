/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.helper.template;

import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyInvitation;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalPreparationForSurveyTemplate implements Serializable {

    private EmployeeInfoKeyResponse employeeInfoKeyResponse;
    private SdssSurveyInvitation singleInvitation;
    private EmailTemplates emailTemplates;
    private SdssSurveyUsers invitedUser;
    private SdssSurveyRound surveyRound;
}
