/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.surveys;

import com.mgdsw.sdss.common.base.lookups.EntityLookupInfo;
import com.mgdsw.sdss.enums.QuestionType;
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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class SdssSurveyQuestion extends EntityLookupInfo<Integer> {

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean showPublicQuestions;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private SdssSurvey survey;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private SdssSurveyRound round;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SdssSurveyQuestionOptions> options;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SdssSurveyUserQuestionAnswers> userQuestionResponses;
}
