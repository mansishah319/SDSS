/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.template;

import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for Creating or Updating Email Templates")
public class EmailTemplatesRequest {

    @Schema(description = "Email Template subjectAr")
    @NotNull(message = FieldNamesUtil.SUBJECT_AR + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.SUBJECT_AR + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String subjectAr;

    @Schema(description = "Email Template contentAr")
    private String contentAr;

    @Schema(description = "Email Template subjectEn")
    @NotNull(message = FieldNamesUtil.SUBJECT_EN + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.SUBJECT_EN + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String subjectEn;

    @Schema(description = "Email Template contentEn")
    private String contentEn;

    @Schema(description = "Email Template Type")
    @NotNull(message = FieldNamesUtil.TEMPLATES_TYPE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private Integer templateTypeId;
    private Boolean isDefault;
}
