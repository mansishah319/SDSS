/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.invitation.InvitationResponse;
import com.mgdsw.sdss.service.ValidatorsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Feb, 2024
 */

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Validator Controller")
@RequestMapping("/api/v1/validator")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class ValidatorController {

    private final ValidatorsService validatorsService;

    @PutMapping(value = "/update-status")
    public ResponseEntity<SuccessResponse<InvitationResponse>> updateInvitationStatus(@RequestParam Integer challengeId,
                                                                                      @RequestParam @NotCommandOrScript String invitationStatus,
                                                                                      @RequestParam(required = false) String reason
    ) {
        return ControllerResponseHelper.okResponse(validatorsService.updateInvitationStatus(challengeId, invitationStatus, reason));
    }
}
