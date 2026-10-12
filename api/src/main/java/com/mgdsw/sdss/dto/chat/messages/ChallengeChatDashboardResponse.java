/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.chat.messages;

import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.dto.user.ChatUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Feb, 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeChatDashboardResponse extends BaseResponse<Integer> {

    private String message;
    private byte[] file;
    private String fromName;
    private long time;
    private String type;
    private String mime;
    private String fileName;
    private int readStatus;
    private ChatUserResponse user;
}
