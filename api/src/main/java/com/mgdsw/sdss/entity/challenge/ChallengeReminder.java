/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.challenge;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.constants.DpConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "reminder")
public class ChallengeReminder extends BaseEntity<Integer> {

    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime reminderTime;

    private boolean isSent;

    @ManyToOne
    @JoinColumn(name = "question_id")
    public Challenges challenges;
}