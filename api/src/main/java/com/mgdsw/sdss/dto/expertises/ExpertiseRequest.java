/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.expertises;

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
 * @createdOn Sep, 2023
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Creating or Updating Expertise")
public class ExpertiseRequest {

    @NotCommandOrScript
    @NotNull(message = FieldNamesUtil.ARABIC_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.ARABIC_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "Expertise Arabic name", example = "الابتكار")
    private String nameAr;

    @NotCommandOrScript
    @NotNull(message = FieldNamesUtil.ENGLISH_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.ENGLISH_NAME + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "Expertise English name", example = "Innovation")
    private String nameEn;
}
