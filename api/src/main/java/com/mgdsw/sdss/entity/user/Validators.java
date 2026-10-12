/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.user;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.enums.ChallengeInvitationType;
import com.mgdsw.sdss.enums.UserEmailInvitationStatus;
import com.mgdsw.sdss.enums.UserInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "validators")
public class Validators extends BaseEntity<Integer> {

    @ManyToOne
    @JoinColumn(name = "question_id")
    public Challenges challenges;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @Enumerated(EnumType.STRING)
    private ChallengeInvitationType inviteType;

    @Enumerated(EnumType.STRING)
    private UserInvitationStatus status;

    @Enumerated(EnumType.STRING)
    private UserEmailInvitationStatus emailInvitationStatus;

    @Column(unique = true)
    private String hashKey;

    private String inviteLink;

    private Boolean isSend;

    private String reason;
}