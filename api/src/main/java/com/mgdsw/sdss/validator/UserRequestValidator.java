/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.validator;

import com.mgdsw.sdss.common.helper.Translator;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomValidationException;
import com.mgdsw.sdss.common.utils.PlaceholderExtractorUtil;
import com.mgdsw.sdss.dto.user.CreateAdminUserRequest;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.AND;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.enums.ErrorCodes.CONSTRAINT_VIOLATION;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
@Component
public class UserRequestValidator {

    private final Validator validator;
    private final Translator translator;

    public UserRequestValidator(Translator translator) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        this.translator = translator;
    }

    public void validate(CreateAdminUserRequest request) {
        Set<ConstraintViolation<CreateAdminUserRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new CustomValidationException(violations.stream().map(violation ->
                            PlaceholderExtractorUtil.extractPlaceholders(violation.getMessage()).stream().map(translator::getMessage)
                                    .collect(Collectors.joining(SPACE)))
                    .collect(Collectors.joining(SPACE + AND + SPACE)),
                    CONSTRAINT_VIOLATION.getErrorCode());
        }
    }
}