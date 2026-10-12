/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.surveys;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgdsw.sdss.common.base.lookups.EntityLookupInfo;
import com.mgdsw.sdss.enums.SdssStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.DateFormat.DATETIME_PATTERN;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"round_number", "sdss_survey_id"}))
public class SdssSurveyRound extends EntityLookupInfo<Integer> {

    @JsonFormat(pattern = DATETIME_PATTERN)
    private LocalDateTime startTime;
    @JsonFormat(pattern = DATETIME_PATTERN)
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private SdssStatus status;
    @Column(name = "round_number")
    private int roundNumber;

    @Column(nullable = false, columnDefinition = "NUMBER(1) DEFAULT 0")
    private Boolean active;

    @Column(nullable = false, columnDefinition = "NUMBER(1) DEFAULT 0")
    private Boolean manualHandle; // If the moderator needs to handle the start survey or end it by himself.

    @ManyToOne
    @JoinColumn(name = "sdss_survey_id")
    private SdssSurvey survey;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SdssSurveyQuestion> questions;
}
