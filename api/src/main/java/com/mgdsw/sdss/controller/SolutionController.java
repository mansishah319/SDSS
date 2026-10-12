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
import com.mgdsw.sdss.dto.solution.MergeSolutionByStageRequest;
import com.mgdsw.sdss.dto.solution.SolutionCreationRequest;
import com.mgdsw.sdss.dto.solution.SolutionFullResponse;
import com.mgdsw.sdss.dto.solution.SolutionResponse;
import com.mgdsw.sdss.dto.solution.UpdateSolutionRequest;
import com.mgdsw.sdss.service.SolutionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
 * @createdOn Feb, 2024
 */

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/solution")
@Tag(name = "Solution Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class SolutionController {

    private final SolutionService solutionService;

    @PostMapping
    public ResponseEntity<SuccessResponse<SolutionResponse>> create(@Valid @RequestBody SolutionCreationRequest request) {
        return ControllerResponseHelper.okResponse(solutionService.create(request));
    }

    @PostMapping(value = "/merge")
    public ResponseEntity<SuccessResponse<SolutionResponse>> mergeSolutions(@Valid @RequestBody MergeSolutionByStageRequest request) {
        return ControllerResponseHelper.okResponse(solutionService.mergeSolutions(request));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<SolutionResponse>> create(@RequestParam Integer solutionId, @Valid @RequestBody UpdateSolutionRequest request) {
        return ControllerResponseHelper.okResponse(solutionService.update(request, solutionId, SolutionResponse.class));
    }

    @GetMapping(value = "/get-by-challenge-id")
    public ResponseEntity<SuccessResponse<List<SolutionFullResponse>>> retrieveAllSolutionByChallengeId(@RequestParam Integer challengeId,
                                                                                                        @RequestParam(required = false) Integer roundLevel) {
        return ControllerResponseHelper.okResponse(solutionService.retrieveAllSolutionByChallengeId(challengeId, roundLevel));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteById(@RequestParam Integer solutionId) {
        return ControllerResponseHelper.okResponse(solutionService.delete(solutionId));
    }

    @PutMapping(value = "/update-round-level")
    public ResponseEntity<SuccessResponse<List<SolutionFullResponse>>> updateRoundLevelByChallengeIdAndSelectedSolutions(@RequestParam(required = false) Integer roundLevel,
                                                                                                                         @RequestParam Integer challengeId,
                                                                                                                         @RequestParam(required = false) Integer isFinal,
                                                                                                                         @RequestParam List<Integer> solutionsListIds) {
        return ControllerResponseHelper.okResponse(solutionService.updateRoundLevelByChallengeIdAndSelectedSolutions(roundLevel, challengeId, isFinal, solutionsListIds));
    }
}
