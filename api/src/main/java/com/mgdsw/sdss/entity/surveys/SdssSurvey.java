/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.surveys;

import com.mgdsw.sdss.common.base.lookups.EntityLookupInfo;
import com.mgdsw.sdss.common.converter.LongListToStringConverter;
import com.mgdsw.sdss.common.converter.StringListToStringConverter;
import com.mgdsw.sdss.enums.SurveyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import java.util.List;

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
public class SdssSurvey extends EntityLookupInfo<Integer> {

    @Column(nullable = false, columnDefinition = "NUMBER(1) DEFAULT 1")
    private Boolean active;
    private Integer numberOfRounds;
    @Column(nullable = false, columnDefinition = "NUMBER(1) DEFAULT 0")
    private Boolean manualHandle;
    @Enumerated(EnumType.STRING)
    private SurveyStatus status;

    @Convert(converter = LongListToStringConverter.class)
    @Column(name = "ALLOWED_GRP_LIST", columnDefinition = "CLOB")
    private List<Long> allowedGrpList;

    @Convert(converter = StringListToStringConverter.class)
    private List<String> jobList;

    @Convert(converter = StringListToStringConverter.class)
    private List<String> departmentList;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SdssSurveyRound> rounds;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SdssSurveyQuestion> questions;
}
