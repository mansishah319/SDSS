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
import com.mgdsw.sdss.dto.survey.SurveyCreationRequest;
import com.mgdsw.sdss.dto.survey.SurveyForSolutionResponse;
import com.mgdsw.sdss.service.SurveyService;
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
 * @createdOn 8月, 2024
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Survey Controller")
@RequestMapping("/api/v1/survey")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<SuccessResponse<SurveyForSolutionResponse>> create(@Valid @RequestBody SurveyCreationRequest request) {
        return ControllerResponseHelper.createResponse(surveyService.createAndUpdate(request));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<SurveyForSolutionResponse>> update(@Valid @RequestBody SurveyCreationRequest request,
                                                                             @RequestParam Integer surveyId) {
        return ControllerResponseHelper.okResponse(surveyService.update(request, surveyId));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<SuccessResponse<List<SurveyForSolutionResponse>>> findAllBySolutionId(@RequestParam Integer solutionId) {
        return ControllerResponseHelper.okResponse(surveyService.findAllBySolutionId(solutionId));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<SurveyForSolutionResponse>> findById(@RequestParam Integer surveyId) {
        return ControllerResponseHelper.okResponse(surveyService.findById(surveyId, SurveyForSolutionResponse.class));
    }

    @GetMapping(value = "/by-solution")
    public ResponseEntity<SuccessResponse<SurveyForSolutionResponse>> findOneByIdSolution(@RequestParam Integer solutionId) {
        return ControllerResponseHelper.okResponse(surveyService.findSurveyBySolutionId(solutionId));
    }
}
