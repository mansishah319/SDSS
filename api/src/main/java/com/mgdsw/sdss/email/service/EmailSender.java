/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.email.service;

import com.mgdsw.sdss.email.dto.EmailDetails;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */

@FunctionalInterface
public interface EmailSender {

    void sendEmail(EmailDetails mail);
}
