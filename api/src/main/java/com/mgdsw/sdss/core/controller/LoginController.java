/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.core.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.core.client.CoreWebClient;
import com.mgdsw.sdss.core.dto.login.LoginRequest;
import com.mgdsw.sdss.core.dto.login.SdssLoginUserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * -----------------------------------------------------
 * * Not used anymore (This is the old scenario for login)
 * -----------------------------------------------------
 *
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 09 Jan 2025
 */

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "SDSS Login Controller")
@RequestMapping("/public/api/v1/core/sdss/login")
public class LoginController {

    private final CoreWebClient coreWebClient;

    @PostMapping
    @Profile(value = "!stg")
    public ResponseEntity<SuccessResponse<SdssLoginUserResponse>> getToken(@Valid @RequestBody LoginRequest request) {
        return ControllerResponseHelper.okResponse(coreWebClient.loginToNtlm(request));
    }

    @PostMapping(value = "/stg")
    @Profile(value = "stg")
    public ResponseEntity<SuccessResponse<SdssLoginUserResponse>> getTokenForStg(@Valid @RequestBody LoginRequest request) {
        return ControllerResponseHelper.okResponse(coreWebClient.loginToNtlmWithExtendedClaims(request));
    }
}
