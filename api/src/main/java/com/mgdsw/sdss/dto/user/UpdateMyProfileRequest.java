/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.user;


import com.mgdsw.sdss.common.annotations.Phone;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMyProfileRequest {

    @Email(message = FieldNamesUtil.EMAIL + SPACE + DpConstants.Messages.FailedMessages.INVALID_EMAIL)
    private String alternativeEmail;
    @Phone(message = FieldNamesUtil.MOBILE_NUMBER + SPACE + DpConstants.Messages.FailedMessages.INVALID_PHONE)
    private String alternativeMobile;
}
