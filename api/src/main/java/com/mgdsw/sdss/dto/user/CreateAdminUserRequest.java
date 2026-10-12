/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import com.mgdsw.sdss.common.annotations.OneIsPrimary;
import com.mgdsw.sdss.common.annotations.Phone;
import com.mgdsw.sdss.common.annotations.UniqueField;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import com.mgdsw.sdss.dto.user.expertises.UsersExpertisesRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.DUPLICATE_RECORD;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Creating or Updating Admin Users")
public class CreateAdminUserRequest implements BaseMarkerDTO {

    @Min(1)
    @NotNull(message = FieldNamesUtil.GRP_NUMBER + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    private Long grpNumber;

    // Login Details
    @NotCommandOrScript
    @NotNull(message = FieldNamesUtil.LANGUAGE + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.LANGUAGE + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    @Pattern(regexp = DpConstants.Regex.CAPITAL_LETTERS, message = FieldNamesUtil.LANGUAGE + SPACE + DpConstants.Messages.FailedMessages.CAPITAL_LETTERS_REQUIRED_ERROR_MESSAGE)
    private String language;
    @NotCommandOrScript
    @Email(message = FieldNamesUtil.EMAIL + SPACE + DpConstants.Messages.FailedMessages.INVALID_EMAIL)
    @NotNull(message = FieldNamesUtil.EMAIL + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.EMAIL + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String email;
    @NotCommandOrScript
    @Email(message = FieldNamesUtil.ALTERNATIVE_EMAIL + SPACE + DpConstants.Messages.FailedMessages.INVALID_EMAIL)
    private String alternativeEmail;
    // Contact Details
    // @Phone(message = FieldNamesUtil.MOBILE_NUMBER + SPACE + DpConstants.Messages.FailedMessages.INVALID_PHONE)
    @NotNull(message = FieldNamesUtil.MOBILE_NUMBER + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.MOBILE_NUMBER + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String mobile;
    @NotCommandOrScript
    @Phone(message = FieldNamesUtil.ALTERNATIVE_MOBILE_NUMBER + SPACE + DpConstants.Messages.FailedMessages.INVALID_PHONE)
    private String alternativeMobile;

    // Permission Details
    private Boolean internalDocuments;
    private Boolean chatContents;
    private Boolean socialMediaFeeds;
    private Boolean decision;
    private Boolean questionView;
    private Boolean scrollingText;
    private Boolean isDelphiModerator;

    @Valid
    @OneIsPrimary
    @UniqueField(fieldName = FieldNamesUtil.FIELD_AREA_EXPERTISES_ID, message = FieldNamesUtil.AREA_EXPERTISES_ID + SPACE + DUPLICATE_RECORD)
    private List<UsersExpertisesRequest> usersExpertises;
    @NotEmpty(message = FieldNamesUtil.ROLES + SPACE + DpConstants.Messages.FailedMessages.NOT_EMPTY_ERROR_MESSAGE)
    private List<Integer> roles;
    @JsonIgnore
    private String baseUrl;
}
