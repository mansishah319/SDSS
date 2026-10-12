/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.surveys;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class SdssSurveyInvitation extends BaseEntity<Integer> {

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private SdssSurvey survey;

    @ManyToOne
    @JoinColumn(name = "survey_round_id")
    private SdssSurveyRound surveyRound;

    @Enumerated(EnumType.STRING)
    private TemplatesTypeKeys templatesTypeKey;

    @ManyToOne
    @JoinColumn(name = "invited_user_id")
    private SdssSurveyUsers invitedUser;

    private String inviteLink;
    private Boolean sent;
}
