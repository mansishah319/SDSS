/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.template.type;

import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

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
public class TemplateTypeRequest {

    @NotCommandOrScript
    @Schema(description = "Template title EN", example = "Challenge Invitation")
    @NotNull(message = FieldNamesUtil.ENGLISH_NAME + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String titleEn;
    @NotCommandOrScript
    @Schema(description = "Template title AR", example = "دعوة تحدي")
    @NotNull(message = FieldNamesUtil.ARABIC_NAME + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String titleAr;
    @NotCommandOrScript
    @Schema(description = "Template Type Key", example = "CHALLENGE_USER_INVITATION")
    @NotNull(message = FieldNamesUtil.CONTENT_AR + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private String templateTypeKey;
    @Schema(description = "Mandatory Fields EN", example = "[\"${user.firstname}\",\"${challenge.titleEn}\",\"${challenge.startTime}\",\"${challenge.startTime}\",\"${challenge.endTime}\",\"${ChallengeUserInvitationUrl}\"]")
    private List<String> mandatoryFieldsEn;
    @Schema(description = "Mandatory Fields AR", example = "[\"${user.firstname}\",\"${challenge.titleAr}\",\"${challenge.startTime}\",\"${challenge.startTime}\",\"${challenge.endTime}\",\"${ChallengeUserInvitationUrl}\"]")
    private List<String> mandatoryFieldsAr;
}
