/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.enums;

import com.mgdsw.sdss.common.utils.DpCommonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;

import static com.mgdsw.sdss.common.constants.DpConstants.Messages.SuccessMessages.MESSAGES;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn May 2024
 */

@Getter
@AllArgsConstructor
public enum LocalizedSuccessCodes {
    // General Messages
    CREATED_SUCCESSFULLY("CREATED_SUCCESSFULLY"),
    UPDATED_SUCCESSFULLY("UPDATED_SUCCESSFULLY"),
    DELETED_SUCCESSFULLY("DELETED_SUCCESSFULLY"),
    DATA_RETRIEVED_SUCCESSFULLY("DATA_RETRIEVED_SUCCESSFULLY"),
    UPLOADED_SUCCESSFULLY("UPLOADED_SUCCESSFULLY"),
    RECORD_FOUND("RECORD_FOUND"),
    SDSS_INVITATION_CONFIRMED_SUCCESSFULLY("SDSS_INVITATION_CONFIRMED_SUCCESSFULLY"),
    OPERATION_COMPLETED("OPERATION_COMPLETED"),
    RESOURCE_AVAILABLE("RESOURCE_AVAILABLE"),
    LOGGED_IN_SUCCESSFULLY("LOGGED_IN_SUCCESSFULLY"),
    LOGGED_OUT_SUCCESSFULLY("LOGGED_OUT_SUCCESSFULLY"),
    PAGINATION_LOGGED_MESSAGE("PAGINATION_LOGGED_MESSAGE"),
    PASSWORD_CHANGED_SUCCESSFULLY("PASSWORD_CHANGED_SUCCESSFULLY"),
    PROFILE_UPDATED_SUCCESSFULLY("PROFILE_UPDATED_SUCCESSFULLY"),
    RECORD_APPROVED_SUCCESSFULLY("RECORD_APPROVED_SUCCESSFULLY");

    private final String successCode;

    public String getLocalizedMessage(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(MESSAGES, locale);
        return bundle.getString(successCode);
    }

    public String getLocalizedMessage() {
        ResourceBundle bundle = ResourceBundle.getBundle(MESSAGES, DpCommonUtil.getUserCurrentLocale());
        return bundle.getString(successCode);
    }
}