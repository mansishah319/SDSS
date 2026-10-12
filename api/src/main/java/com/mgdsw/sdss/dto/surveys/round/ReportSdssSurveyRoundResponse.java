/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys.round;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.utils.DateTimeHandlerUtil;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.ReportSdssSurveyResponse;
import com.mgdsw.sdss.dto.surveys.lookups.LookupInfoResponse;
import com.mgdsw.sdss.dto.surveys.questions.ReportSdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.user.ReportUserSurveyAnswerStatusResponse;
import com.mgdsw.sdss.enums.QuestionType;
import com.mgdsw.sdss.enums.SdssStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.utils.DpDateUtil.DATE_TIME_FORMAT;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportSdssSurveyRoundResponse extends LookupInfoResponse<Integer> {

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime startTime;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime endTime;
    private Boolean manualHandle;
    private SdssStatus status;
    private int roundNumber;
    private Boolean active;
    private long totalNumberOfInvitedUsers;
    private ReportSdssSurveyResponse survey;
    private List<ReportSdssSurveyQuestionResponse> questions;
    private List<ReportUserSurveyAnswerStatusResponse> surveyUsers;
    private List<UserSurveyAnswersReportResponse> usersSurveyAnswers;
    private RoundReportMainResponse roundDetails;

    @JsonProperty("localStatus")
    public String getLocalStatus() {
        return DpCommonUtil.retrieveLocalValue(status.getEn(), status.getAr());
    }

    @JsonProperty("currentUserIsTheSurveyModerator")
    public Boolean currentUserIsTheSurveyModerator() {
        return DpCommonUtil.retrieveCurrentUserGrpNumber().equals(Long.valueOf(super.getCreatedBy()));
    }

    @JsonProperty("isAllowedToUpdateOrDelete")
    public Boolean isAllowedToUpdateOrDeleteThisSurveyRound() {
        return status.equals(SdssStatus.PENDING);
    }

    @JsonProperty("numberOfQuestions")
    public Integer numberOfQuestionsForThisSurveyRound() {
        return questions != null ? questions.size() : 0;
    }

    @JsonProperty("totalTextQuestions")
    public Integer getTotalOfQuestionsTypeText() {
        return getTotalOfQuestionsByType(QuestionType.TEXT);
    }

    @JsonProperty("totalMultipleChoiceQuestions")
    public Integer getTotalOfQuestionsTypeMultipleChoice() {
        return getTotalOfQuestionsByType(QuestionType.MULTI_SELECT);
    }

    @JsonProperty("totalSingleSelectQuestions")
    public Integer getTotalOfQuestionsTypeSingleSelect() {
        return getTotalOfQuestionsByType(QuestionType.SINGLE_SELECT);
    }

    @JsonProperty("totalTrueFalseQuestions")
    public Integer getTotalOfQuestionsTypeTrueFalse() {
        return getTotalOfQuestionsByType(QuestionType.TRUE_FALSE);
    }

    @JsonProperty("retrieveAllTextQuestions")
    public List<UserSurveyAnswersReportResponse> retrieveAllTextQuestions() {
        return filterByQuestionType(QuestionType.TEXT);
    }

    @JsonProperty("currentYear")
    public Integer getCurrentYear() {
        return DateTimeHandlerUtil.getCurrentYear();
    }

    private Integer getTotalOfQuestionsByType(QuestionType type) {
        return questions != null
                ? (int) questions.stream()
                        .filter(q -> q.getQuestionType() == type)
                        .count()
                : 0;
    }

    private List<UserSurveyAnswersReportResponse> filterByQuestionType(QuestionType questionType) {
        if (usersSurveyAnswers == null || questionType == null) {
            return Collections.emptyList();
        }

        return usersSurveyAnswers.stream()
                .filter(response -> response.getQuestion() != null
                        && response.getQuestion().getQuestionType() == questionType)
                .collect(Collectors.toList());
    }
}
