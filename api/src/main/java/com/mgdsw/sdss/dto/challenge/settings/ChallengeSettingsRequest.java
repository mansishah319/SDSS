/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.challenge.settings;

import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Creating or Updating ChallengeSettings")
public class ChallengeSettingsRequest {

    @NotCommandOrScript
    @NotNull(message = FieldNamesUtil.CHALLENGE_SETTINGS_TYPE + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.CHALLENGE_SETTINGS_TYPE + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String type;

    @NotCommandOrScript
    @NotNull(message = FieldNamesUtil.NAME_EN + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.NAME_EN + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String nameEn;

    @NotCommandOrScript
    @NotNull(message = FieldNamesUtil.NAME_AR + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.NAME_AR + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String nameAr;
}
