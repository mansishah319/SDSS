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
import com.mgdsw.sdss.dto.video.VideosDashboardResponse;
import com.mgdsw.sdss.service.ChallengeVideoService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 8月, 2024
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Youtube Controller")
@RequestMapping("/api/v1/youtube")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class YoutubeVideoController {

    private final ChallengeVideoService challengeVideoService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<VideosDashboardResponse>>> retrieveAllYoutubeVideosByChallengeId(
            @Parameter(
                    explode = Explode.TRUE,
                    name = "challengeId",
                    in = ParameterIn.QUERY,
                    required = false,
                    description = "Optional challengeId to filter YouTube Videos by a specific challenge.",
                    style = ParameterStyle.FORM,
                    schema = @Schema(type = "Integer", defaultValue = "0")
            )
            @RequestParam(value = "challengeId", required = false) Integer challengeId) {
        return ControllerResponseHelper.okResponse(challengeVideoService.retrieveAllYoutubeVideosByChallengeId(challengeId));
    }
}
