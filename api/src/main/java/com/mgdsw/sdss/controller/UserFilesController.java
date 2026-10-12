/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.service.UserFilesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 15 Jan 2025
 */

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Files Controller")
@RequestMapping("/api/v1/files")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class UserFilesController {

    private final UserFilesService userFilesService;

    @GetMapping("/static/user-manual/download")
    public ResponseEntity<Resource> downloadUserManualFile() {
        Resource resource = userFilesService.downloadFile("SDSS_UserManual.pdf");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
