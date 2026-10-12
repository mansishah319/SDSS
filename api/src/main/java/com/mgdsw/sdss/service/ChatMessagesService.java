/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.chat.messages.ChallengeChatDashboardResponse;
import com.mgdsw.sdss.dto.chat.messages.ChatMessagesResponse;
import com.mgdsw.sdss.entity.chat.ChatMessages;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface ChatMessagesService extends BaseService<Integer, ChatMessages> {

    ChatMessagesResponse createNewChatMessage(@NotCommandOrScript String message, int challengeId);

    ChatMessagesResponse uploadNewChatFiles(MultipartFile file, int challengeId);

    List<ChallengeChatDashboardResponse> retrieveAllChatMessagesByChallengeId(int challengeId);
}