/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys.questions.options;

import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;

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
public class UpdateSurveyQuestionOptionsRequest {

    private Integer id;
    @NotNull(message = FieldNamesUtil.OPTION_NAME_ENGLISH + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.OPTION_NAME_ENGLISH + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String optionEn;
    @NotNull(message = FieldNamesUtil.OPTION_NAME_ARABIC + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.OPTION_NAME_ARABIC + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String optionAr;
}
