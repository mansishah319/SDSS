/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.chat;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.entity.challenge.Challenges;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_room")
public class ChatRoom extends BaseEntity<Integer> {

    private String chatName;
    private String idHash;
    private String ownerId;
    private String ownerName;
    private long lastMessageTime;
    private long type;
    private String groupPic;
    private String tempToken;
    private long time;
    private String lastMessage;

    @ManyToOne
    @JoinColumn(name = "question_id")
    public Challenges challenges;
}