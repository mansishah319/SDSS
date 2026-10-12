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
import com.mgdsw.sdss.dto.feedback.CommentReplyResponse;
import com.mgdsw.sdss.dto.feedback.CreateCommentReplyRequest;
import com.mgdsw.sdss.dto.feedback.UpdateCommentReplyRequest;
import com.mgdsw.sdss.service.CommentReplyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Feb, 2024
 */

@Validated
@RestController
@RequestMapping("/api/v1/solution/comments/reply")
@Tag(name = "Solution Comment Reply Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class CommentReplyController {

    private final CommentReplyService commentReplyService;

    @PostMapping
    public ResponseEntity<SuccessResponse<CommentReplyResponse>> create(@Valid CreateCommentReplyRequest request) {
        return ControllerResponseHelper.okResponse(commentReplyService.create(request));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<CommentReplyResponse>> update(@RequestParam Integer replyId, @Valid @RequestBody UpdateCommentReplyRequest request) {
        return ControllerResponseHelper.okResponse(commentReplyService.update(request, replyId, CommentReplyResponse.class));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteById(@RequestParam Integer commentId) {
        return ControllerResponseHelper.okResponse(commentReplyService.delete(commentId));
    }
}
