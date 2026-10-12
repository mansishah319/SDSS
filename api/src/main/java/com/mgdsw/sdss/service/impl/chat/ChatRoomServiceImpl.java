/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.chat;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.chat.ChatRoom;
import com.mgdsw.sdss.repository.chat.ChatRoomRepository;
import com.mgdsw.sdss.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl extends AbstractBaseServiceImpl<Integer, ChatRoom> implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    protected ChatRoomRepository repository() {
        return chatRoomRepository;
    }
}