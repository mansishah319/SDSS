/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.chat;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.chat.ChatMembers;
import com.mgdsw.sdss.repository.chat.ChatMembersRepository;
import com.mgdsw.sdss.service.ChatMembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class ChatMembersServiceImpl extends AbstractBaseServiceImpl<Integer, ChatMembers> implements ChatMembersService {

    private final ChatMembersRepository chatMembersRepository;

    @Override
    protected ChatMembersRepository repository() {
        return chatMembersRepository;
    }
}