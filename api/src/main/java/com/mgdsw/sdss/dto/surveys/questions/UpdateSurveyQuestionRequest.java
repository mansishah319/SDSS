/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys.questions;

import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing an update survey question request")
public class UpdateSurveyQuestionRequest {

    @NotNull(message = FieldNamesUtil.TITLE_EN + SPACE + NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.TITLE_EN + SPACE + NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "Title in English", example = "What is Your Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String titleEn;

    @NotNull(message = FieldNamesUtil.TITLE_AR + SPACE + NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.TITLE_AR + SPACE + NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "Title in Arabic", example = "ما اسمك؟", requiredMode = Schema.RequiredMode.REQUIRED)
    private String titleAr;

    @Schema(description = "Description in English", example = "Please provide your name.")
    private String descriptionEn;

    @Schema(description = "Description in Arabic", example = "يرجى تقديم اسمك.")
    private String descriptionAr;

    @NotNull(message = FieldNamesUtil.QUESTION_TYPE + SPACE + NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.QUESTION_TYPE + SPACE + NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "Type of the question", example = "text", requiredMode = Schema.RequiredMode.REQUIRED)
    private String questionType;

    private Boolean showPublicQuestions;
}