/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.committee.controller;

import com.mgdsw.sdss.committee.dto.CommitteeResponse;
import com.mgdsw.sdss.committee.service.CommitteeService;
import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Mar, 2024
 */

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
@Tag(name = "CommitteeController")
@RequestMapping("/api/v1/committee")
public class CommitteeController {

    private final CommitteeService committeeService;

    @GetMapping(value = "/dropdown-list")
    public ResponseEntity<SuccessResponse<List<CommitteeResponse>>> retrieveAllCommittees() {
        return ControllerResponseHelper.okResponse(committeeService.retrieveAllCommittees());
    }
}
