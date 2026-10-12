/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.common.utils.CryptoUtil;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.dto.surveys.user.PublicCurrentInvitedSurveyUserResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 26 Nov 2024
 */

@Component
@RequiredArgsConstructor
public class InvitedSurveyUserMapper {

    private final CryptoUtil cryptoUtil;

    public PublicCurrentInvitedSurveyUserResponse toPublicEndUserResponse(SdssSurveyUsers surveyUser) {
        if (surveyUser == null) {
            return null;
        }
        return PublicCurrentInvitedSurveyUserResponse.builder()
                .employeeInfo(cryptoUtil.decrypt(surveyUser.getEmployeeInfoKey(), EmployeeInfoKeyResponse.class))
                .employeeGrpNumber(surveyUser.getEmployeeGrpNumber())
                .id(surveyUser.getId())
                .build();
    }
}
