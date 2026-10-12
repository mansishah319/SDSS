/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.sectors;

import com.mgdsw.sdss.common.annotations.AlphaNum;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Creating or Updating Sectors")
public class CreateSectorsRequest implements BaseMarkerDTO {

    @NotNull(message = FieldNamesUtil.ARABIC_NAME + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.ARABIC_NAME + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "Sector Arabic name", example = "شركة كبرى", pattern = DpConstants.Regex.ARABIC_WORDS_AND_NUMBERS_ONLY)
    @AlphaNum(allowedChars = DpConstants.Regex.SPACE_HYPHEN_AND_ALLOWED_CHARS)
    @Pattern(
            regexp = DpConstants.Regex.ARABIC_WORDS_AND_NUMBERS_ONLY,
            message = DpConstants.Regex.Message.ARABIC_WORDS_AND_NUMBERS_ONLY
    )
    private String nameAr;

    @NotNull(message = FieldNamesUtil.ENGLISH_NAME + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.ENGLISH_NAME + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "Sector English name", example = "Corporate", pattern = DpConstants.Regex.START_EN_WORDS_AND_NUMBERS_BETWEEN)
    @AlphaNum(allowedChars = DpConstants.Regex.SPACE_HYPHEN_AND_ALLOWED_CHARS)
    @Pattern(
            regexp = DpConstants.Regex.START_EN_WORDS_AND_NUMBERS_BETWEEN,
            message = DpConstants.Regex.Message.START_EN_WORDS_AND_NUMBERS_BETWEEN
    )
    private String nameEn;
}
