/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.challenge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import com.mgdsw.sdss.common.annotations.ValidURLList;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.DpDateUtil;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.END_TIME_FUTURE_ERROR;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.REMINDERS_TIME_FUTURE_ERROR;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.START_END_TIME_ERROR;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.START_TIME_FUTURE_ERROR;
import static com.mgdsw.sdss.common.utils.DpDateUtil.DATE_TIME_FORMAT;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Creating or Updating Challenges")
public class ChallengesCreationRequest implements BaseMarkerDTO {

    @Schema(description = "Invited Users IDs", example = "[37075]")
    private List<Long> invitedUsersIds;

    @Schema(description = "Invited Users IDs", example = "[37075]")
    private List<Long> validatorsIds;

    @Schema(description = "Moderator ID", example = "37075")
    @NotNull(message = FieldNamesUtil.MODERATOR_ID + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private Long moderatorId;

    @Schema(description = "Assistant Moderator ID", example = "37075")
    private Long assistantModeratorGrp;

    @Schema(description = "Department ID", example = "11486")
    @NotCommandOrScript
    @NotNull(message = FieldNamesUtil.DEPARTMENT_ID + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String departmentId;
    @Schema(description = "Department Name", example = "آلإدارة العامة للتحريات والمباحث الجنائية")
    @NotNull(message = FieldNamesUtil.DEPARTMENT_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String departmentName;

    @NotCommandOrScript
    @Schema(description = "SubDepartment ID", example = "11509")
    @NotNull(message = FieldNamesUtil.DEPARTMENT_ID + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String subDepartmentId;
    @NotCommandOrScript
    @Schema(description = "SubDepartment Name", example = "أدارة البحث الجنائي")
    @NotNull(message = FieldNamesUtil.DEPARTMENT_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String subDepartmentName;

    @NotCommandOrScript
    @Schema(description = "Section ID", example = "28416")
    @NotNull(message = FieldNamesUtil.DEPARTMENT_ID + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String sectionId;
    @NotCommandOrScript
    @Schema(description = "Section Name", example = "مباحث القصيص")
    @NotNull(message = FieldNamesUtil.DEPARTMENT_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String sectionName;

    @NotNull(message = FieldNamesUtil.START_TIME + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @Schema(description = "Start Time", example = "2030-04-08 12:30:00")
    @JsonFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private String startTime;
    @NotNull(message = FieldNamesUtil.END_TIME + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @Schema(description = "End Time", example = "2030-04-08 01:30:00")
    @JsonFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private String endTime;

    @NotCommandOrScript
    @Schema(description = "Visibility", example = "PRIVATE")
    @NotNull(message = FieldNamesUtil.VISIBILITY + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String visibility;

    private List<String> hashtagTexts;

    @NotNull(message = FieldNamesUtil.ALLOW_IMMEDIATE_ACCESS + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private boolean allowImmediateAccess;

    @Schema(description = "strategicGoalsId", example = "21750")
    private Integer strategicGoalsId;
    @Schema(description = "strategicDirectionId", example = "426")
    private Integer strategicDirectionId;

    @Schema(description = "futureStrategicGoalsId", example = "1")
    private Integer futureStrategicGoalsId;
    @Schema(description = "futureStrategicDirectionId", example = "1")
    private Integer futureStrategicDirectionId;

    @ValidURLList
    private List<String> youtubeLinks;

    @NotCommandOrScript
    @Size(min = 1, max = 500)
    @NotNull(message = FieldNamesUtil.ARABIC_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.ARABIC_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "Challenge Arabic name", example = "اجراءات الموظفين للكوفيد 19", pattern = DpConstants.Regex.ARABIC_WORDS_AND_NUMBERS_ONLY)
    private String titleAr;
    @NotCommandOrScript
    @Size(min = 1, max = 500)
    @NotNull(message = FieldNamesUtil.ENGLISH_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.ENGLISH_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "Challenge English name", example = "COVID-19 staff procedures", pattern = DpConstants.Regex.START_EN_WORDS_AND_NUMBERS_BETWEEN)
    private String titleEn;

    @NotCommandOrScript
    @NotNull(message = FieldNamesUtil.DESCRIPTION_ARABIC + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @Schema(description = "Challenge Description Ar", example = "الإجراءات اللازمة للموظفين في وجود كوفيد 19")
    @Size(min = 1, max = 1000)
    private String descriptionAr;
    @NotCommandOrScript
    @NotNull(message = FieldNamesUtil.DESCRIPTION_ENGLISH + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @Schema(description = "Challenge Description EN", example = "What should we do for COVID-19 fo staff procedures")
    @Size(min = 1, max = 1000)
    private String descriptionEn;

    private List<String> challengeRemindersDateTimeList;
    private List<String> solutionsTexts;
    private List<Integer> documentsIds;

    @NotCommandOrScript
    private String topicClassification;
    @NotCommandOrScript
    private String levelOfDecision;
    @NotCommandOrScript
    private String importanceOfDecision;
    @NotCommandOrScript
    private String impact;
    @NotCommandOrScript
    private String field;
    private boolean urgent;

    private Integer committeeId;

    @JsonIgnore
    @AssertTrue(message = START_END_TIME_ERROR)
    public boolean isStartTimeBeforeEndTime() {
        return DpDateUtil.isStartDateTimeBeforeEndDateTime(startTime, endTime, DATE_TIME_FORMAT);
    }

    @JsonIgnore
    @AssertTrue(message = START_TIME_FUTURE_ERROR)
    public boolean isStartTimeAfterCurrentDate() {
        return DpDateUtil.isDateTimeAfterCurrentDateTime(startTime, DATE_TIME_FORMAT);
    }

    @JsonIgnore
    @AssertTrue(message = END_TIME_FUTURE_ERROR)
    public boolean isEndTimeAfterCurrentDate() {
        return DpDateUtil.isDateTimeAfterCurrentDateTime(endTime, DATE_TIME_FORMAT);
    }

    @JsonIgnore
    @AssertTrue(message = REMINDERS_TIME_FUTURE_ERROR)
    public boolean isAllRemindersInFuture() {
        return challengeRemindersDateTimeList == null || challengeRemindersDateTimeList.isEmpty() ||
                challengeRemindersDateTimeList.stream()
                        .allMatch(reminderTime -> DpDateUtil.isDateTimeAfterCurrentDateTime(reminderTime, DpConstants.DateFormat.DATETIME_PATTERN));
    }
}
