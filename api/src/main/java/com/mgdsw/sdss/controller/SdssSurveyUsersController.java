/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.common.security.InputValidationUtil;
import com.mgdsw.sdss.service.SdssSurveyUsersService;
import com.mgdsw.sdss.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.regex.Pattern;


/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 26 Nov 2024
 */

@Validated
@RestController
@RequestMapping("/v1/api/v1/survey-users")
@Tag(name = "Sdss Survey Users Controller")
@RequiredArgsConstructor
public class SdssSurveyUsersController {

    private final SdssSurveyUsersService service;
    private final InputValidationUtil inputValidationUtil;
    private final UserService userService;

    // Pattern to match only numeric characters (including negative numbers)
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^-?\\d+$");

    @GetMapping(value = "/public/login-survey-access-key")
    public ResponseEntity<SuccessResponse<String>> loginSurveyAccessKey(@RequestHeader(name = "Authorization", required = false) String existUserToken,
                                                                        @RequestParam(required = false) Long grpNumber,
                                                                        @RequestParam(required = false) String accessKey) {

        // Validate grpNumber if provided
        if (Objects.nonNull(grpNumber)) {
            String grpNumberStr = String.valueOf(grpNumber);
            if (grpNumberStr != null && !grpNumberStr.trim().isEmpty()) {
                grpNumber = validateAndConvertGrpNumber(grpNumberStr);
            }
        }

        return ControllerResponseHelper.okResponse(service.loginSurveyAccessKey(existUserToken, grpNumber, accessKey));
    }

    private Long validateAndConvertGrpNumber(String grpNumberStr) {
        // Check for dangerous content first
        if (!inputValidationUtil.isValidInput(grpNumberStr)) {
            throw new CustomDataException(
                    "Invalid input detected. The provided value contains unsafe or disallowed characters.",
                    "2564"
            );
        }

        // Check if the input contains only numeric characters
        if (!NUMERIC_PATTERN.matcher(grpNumberStr.trim()).matches()) {
            throw new CustomDataException(
                    "Group number must contain only numeric characters.",
                    "2564"
            );
        }

        // Check if the numeric value is within the valid range
        try {
            long numericValue = Long.parseLong(grpNumberStr.trim());
            if (numericValue < 1) {
                throw new CustomDataException(
                        "Group number must be greater than zero.",
                        "2564"
                );
            }
            return numericValue;
        } catch (NumberFormatException e) {
            throw new CustomDataException(
                    "Unable to process the group number. Please provide a valid numeric value.",
                    "2564"
            );
        }
    }
}
