/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.chat;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.security.FileSecurityValidator;
import com.mgdsw.sdss.dto.chat.messages.ChallengeChatDashboardResponse;
import com.mgdsw.sdss.dto.chat.messages.ChatMessagesResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.chat.ChatMessages;
import com.mgdsw.sdss.enums.ChatMessagesTypes;
import com.mgdsw.sdss.repository.chat.ChatMessagesRepository;
import com.mgdsw.sdss.service.ChallengesService;
import com.mgdsw.sdss.service.ChatMessagesService;
import com.mgdsw.sdss.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
@Service
public class ChatMessagesServiceImpl extends AbstractBaseServiceImpl<Integer, ChatMessages> implements ChatMessagesService {

    private final ChatMessagesRepository chatMessagesRepository;
    private final ChallengesService challengesService;
    private final UserService userService;
    private final FileSecurityValidator fileSecurityValidator;

    public ChatMessagesServiceImpl(ChatMessagesRepository chatMessagesRepository,
                                   @Lazy ChallengesService challengesService,
                                   UserService userService,
                                   FileSecurityValidator fileSecurityValidator) {
        this.chatMessagesRepository = chatMessagesRepository;
        this.challengesService = challengesService;
        this.userService = userService;
        this.fileSecurityValidator = fileSecurityValidator;
    }

    @Override
    protected ChatMessagesRepository repository() {
        return chatMessagesRepository;
    }

    @Override
    public ChatMessagesResponse createNewChatMessage(String message, int challengeId) {
        ChatMessages chatMessages = chatMessagesRepository.save(
                ChatMessages.builder()
                        .message(message)
                        .user(userService.retrieveCurrentUser())
                        .challenges(this.retrieveChallengeById(challengeId))
                        .type(String.valueOf(ChatMessagesTypes.TEXT))
                        .readStatus(0)
                        .build()
        );
        return super.modelMapper.map(chatMessages, ChatMessagesResponse.class);
    }

    @Override
    public ChatMessagesResponse uploadNewChatFiles(MultipartFile file, int challengeId) {
        try {
            // Validate file security before processing
            fileSecurityValidator.validateFileSecurity(file);

            ChatMessages chatMessages = chatMessagesRepository.save(
                    ChatMessages.builder()
                            .message(null)
                            .file(file.getBytes())
                            .fileName(file.getOriginalFilename())
                            .user(userService.retrieveCurrentUser())
                            .challenges(this.retrieveChallengeById(challengeId))
                            .type(FilenameUtils.getExtension(file.getOriginalFilename()))
                            .readStatus(0)
                            .build()
            );
            return super.modelMapper.map(chatMessages, ChatMessagesResponse.class);
        } catch (IOException e) {
            throw new CustomDataException(e.getMessage(), ErrorCodes.UPLOADED_ERROR.getErrorCode());
        }
    }

    @Override
    public List<ChallengeChatDashboardResponse> retrieveAllChatMessagesByChallengeId(int challengeId) {
        return chatMessagesRepository.findAllByChallenges(this.retrieveChallengeById(
                        challengeId), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "createdAt")))
                .stream().map(msg -> modelMapper.map(msg, ChallengeChatDashboardResponse.class)).collect(Collectors.toList());
    }

    private Challenges retrieveChallengeById(int challengeId) {
        return challengesService.findById(challengeId);
    }
}