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
import com.mgdsw.sdss.dto.chat.messages.ChallengeChatDashboardResponse;
import com.mgdsw.sdss.dto.chat.messages.ChatMessagesResponse;
import com.mgdsw.sdss.service.ChatMessagesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Feb, 2024
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Chat Messages Controller")
@RequestMapping("/api/v1/chat/messages")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class ChatMessagesController {

    private final ChatMessagesService chatMessagesService;

    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ChatMessagesResponse>> uploadNewChatFiles(@RequestParam MultipartFile file, @RequestParam int challengeId) {
        return ControllerResponseHelper.createResponse(chatMessagesService.uploadNewChatFiles(file, challengeId));
    }

    @GetMapping(value = "/ordered-by-creation-date")
    public ResponseEntity<SuccessResponse<List<ChallengeChatDashboardResponse>>> retrieveAllChatMessagesByChallengeId(@RequestParam int challengeId) {
        return ControllerResponseHelper.createResponse(chatMessagesService.retrieveAllChatMessagesByChallengeId(challengeId));
    }
}