/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.template.type.TemplateKeysResponse;
import com.mgdsw.sdss.dto.template.type.TemplateTypeRequest;
import com.mgdsw.sdss.dto.template.type.TemplateTypeResponse;
import com.mgdsw.sdss.service.TemplateTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Template Type Controller")
@RequestMapping("/api/v1/template/types")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class TemplateTypeController {

    private final TemplateTypeService templateTypeService;

    @PostMapping
    public ResponseEntity<SuccessResponse<TemplateTypeResponse>> createTemplateType(@Valid @RequestBody TemplateTypeRequest request) {
        return ControllerResponseHelper.createResponse(templateTypeService.createTemplateType(request));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<TemplateTypeResponse>> updateTemplateType(@RequestParam Integer templateTypeId,
                                                                                    @Valid @RequestBody TemplateTypeRequest request) {
        return ControllerResponseHelper.okResponse(templateTypeService.updateTemplateType(templateTypeId, request));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<TemplateTypeResponse>> findTemplateTypeById(@RequestParam Integer templateTypeId) {
        return ControllerResponseHelper.okResponse(templateTypeService.findById(templateTypeId, TemplateTypeResponse.class));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<SuccessResponse<List<TemplateTypeResponse>>> findAllTemplateTypes() {
        return ControllerResponseHelper.okResponse(templateTypeService.getAll(TemplateTypeResponse.class));
    }

    @GetMapping("/keys")
    public ResponseEntity<SuccessResponse<List<TemplateKeysResponse>>> getTemplateKeys() {
        return ControllerResponseHelper.okResponse(templateTypeService.getTemplateKeys());
    }
}
