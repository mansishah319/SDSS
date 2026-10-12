/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.chat;

import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_messages")
public class ChatMessages extends BaseEntity<Integer> {

    @ManyToOne
    @JoinColumn(name = "chat_room", nullable = false)
    private Challenges challenges;

    @ManyToOne
    @JoinColumn(name = "from_id", nullable = false)
    private User user;

    @NotCommandOrScript
    private String message;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_base64", columnDefinition = "BLOB")
    private byte[] file;
    private String fromName;
    private long time;
    private String type;
    private String mime;
    private String fileName;
    private int readStatus;
    @Enumerated(EnumType.STRING)
    private MessageType chatType;
    private String chatArea;
}