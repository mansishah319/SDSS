/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.media.service.MediaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Media Controller")
@RequestMapping("/api/v1/media")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(value = "/images/upload", consumes = "multipart/form-data")
    public ResponseEntity<SuccessResponse<String>> uploadImage(@RequestParam("image") MultipartFile image) {
        return ControllerResponseHelper.okResponse(mediaService.retrieveBase64Images(image));
    }
}
