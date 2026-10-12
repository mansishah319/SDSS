/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.helper;

import com.mgdsw.sdss.common.utils.DpCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class Translator {

    private final MessageSource messageSource;

    public String getMessage(String msgCode) {
        return messageSource.getMessage(msgCode, null, DpCommonUtil.getUserCurrentLocale());
    }

    public String getMessage(String msgCode, Locale locale) {
        return messageSource.getMessage(msgCode, null, locale);
    }

    public String getMessage(String msgCode, List<Object> mappedMessages) {
        return messageSource.getMessage(msgCode, mappedMessages.toArray(new Object[0]), DpCommonUtil.getUserCurrentLocale());
    }
}
