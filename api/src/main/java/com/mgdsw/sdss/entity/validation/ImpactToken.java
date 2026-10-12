/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.validation;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "impact_token")
public class ImpactToken extends BaseEntity<Integer> {

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    public Challenges challenges;

    private String impctToken;

    @Column(name = "deleted_at")
    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime deletedAt;
}