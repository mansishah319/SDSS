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
import com.mgdsw.sdss.dto.template.EmailTemplatesRequest;
import com.mgdsw.sdss.dto.template.EmailTemplatesResponse;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.service.EmailTemplatesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Tag(name = "Email Templates Controller")
@RequestMapping("/api/v1/email/templates")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class EmailTemplatesController {

    private final EmailTemplatesService emailTemplatesService;

    @PostMapping
    public ResponseEntity<SuccessResponse<EmailTemplatesResponse>> createNewEmailTemplate(@Valid @RequestBody EmailTemplatesRequest request) {
        return ControllerResponseHelper.createResponse(emailTemplatesService.createNewEmailTemplate(request));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<EmailTemplatesResponse>> updateEmailTemplate(@Valid @RequestBody EmailTemplatesRequest request,
                                                                                       @RequestParam Integer templateId) {
        return ControllerResponseHelper.okResponse(emailTemplatesService.updateEmailTemplate(request, templateId));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<EmailTemplatesResponse>> findEmailTemplateById(@RequestParam Integer templateId) {
        return ControllerResponseHelper.okResponse(emailTemplatesService.findById(templateId, EmailTemplatesResponse.class));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<SuccessResponse<List<EmailTemplatesResponse>>> findAllEmailTemplates() {
        return ControllerResponseHelper.okResponse(emailTemplatesService.getAll(EmailTemplatesResponse.class));
    }

    @GetMapping("/active")
    public ResponseEntity<SuccessResponse<EmailTemplates>> retrieveActiveEmailTemplatesByTemplateType(@RequestParam String templateKey) {
        return ControllerResponseHelper.okResponse(emailTemplatesService.retrieveActiveEmailTemplatesByTemplateType(templateKey));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteEmailTemplate(@RequestParam Integer templateId) {
        return ControllerResponseHelper.okResponse(emailTemplatesService.delete(templateId));
    }
}
