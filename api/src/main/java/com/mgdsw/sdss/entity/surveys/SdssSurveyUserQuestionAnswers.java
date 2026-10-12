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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(
        name = "sdss_survey_user_question_answers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"survey_user_id", "survey_question_id"})
        }
)
public class SdssSurveyUserQuestionAnswers extends BaseEntity<Integer> {

    @Column(length = 2000)
    private String responseText;
    private Boolean correct;

    private Double probabilityOfOccurrence;
    private Double impactSeverity;

    @ManyToOne
    @JoinColumn(name = "survey_question_id", nullable = false)
    private SdssSurveyQuestion question;

    @ManyToMany
    @JoinTable(
            name = "sdss_user_survey_question_options",
            joinColumns = @JoinColumn(name = "user_response_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<SdssSurveyQuestionOptions> selectedOptions;

    @ManyToOne
    @JoinColumn(name = "survey_user_id", nullable = false)
    private SdssSurveyUsers user;
}
