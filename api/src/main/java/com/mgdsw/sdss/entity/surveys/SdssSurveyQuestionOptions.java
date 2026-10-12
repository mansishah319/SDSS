/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.surveys;

import com.mgdsw.sdss.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
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
public class SdssSurveyQuestionOptions extends BaseEntity<Integer> {

    private String optionEn;
    private String optionAr;

    @ManyToOne
    @JoinColumn(name = "survey_question_id")
    private SdssSurveyQuestion question;
}
