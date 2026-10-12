/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.chat.messages;

import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Mar, 2024
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageRequest implements BaseMarkerDTO {
    private MessageType type;
    private int challengeId;
    private String content;
    private String sender;
    private String chatArea;
}
