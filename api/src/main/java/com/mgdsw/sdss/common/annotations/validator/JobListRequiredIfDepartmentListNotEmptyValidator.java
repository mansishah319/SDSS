/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.JobListRequiredIfDepartmentListNotEmpty;
import com.mgdsw.sdss.dto.surveys.SdssSurveyCreationRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 24 Feb 2025
 */
public class JobListRequiredIfDepartmentListNotEmptyValidator implements ConstraintValidator<JobListRequiredIfDepartmentListNotEmpty, SdssSurveyCreationRequest> {

    @Override
    public boolean isValid(SdssSurveyCreationRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Null object is considered valid
        }

        List<Long> allowedGrpList = request.getAllowedGrpList();
        return allowedGrpList != null && !allowedGrpList.isEmpty();
    }
}
