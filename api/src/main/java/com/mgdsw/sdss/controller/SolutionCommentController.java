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
import com.mgdsw.sdss.dto.feedback.CreateSolutionCommentRequest;
import com.mgdsw.sdss.dto.feedback.SolutionCommentResponse;
import com.mgdsw.sdss.dto.feedback.UpdateSolutionCommentRequest;
import com.mgdsw.sdss.service.SolutionCommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

@Validated
@RestController
@RequestMapping("/api/v1/solution/comments")
@Tag(name = "Solution Comment Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class SolutionCommentController {

    private final SolutionCommentService solutionCommentService;

    @PostMapping
    public ResponseEntity<SuccessResponse<SolutionCommentResponse>> create(@Valid @RequestBody CreateSolutionCommentRequest request) {
        return ControllerResponseHelper.okResponse(solutionCommentService.create(request));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<SolutionCommentResponse>> update(@RequestParam Integer commentId, @Valid @RequestBody UpdateSolutionCommentRequest request) {
        return ControllerResponseHelper.okResponse(solutionCommentService.update(request, commentId, SolutionCommentResponse.class));
    }

    @GetMapping(value = "/get-all-by-solution-id")
    public ResponseEntity<SuccessResponse<List<SolutionCommentResponse>>> findAllBySolutionId(@RequestParam Integer solutionId) {
        return ControllerResponseHelper.okResponse(solutionCommentService.getAllBySolutionId(solutionId));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteById(@RequestParam Integer commentId) {
        return ControllerResponseHelper.okResponse(solutionCommentService.delete(commentId));
    }
}
