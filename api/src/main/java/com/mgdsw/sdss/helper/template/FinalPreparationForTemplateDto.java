/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.helper.template;

import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2024
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalPreparationForTemplateDto implements Serializable {

    private TemplatesTypeKeys templatesTypeKeys;
    private EmailTemplates emailTemplates;
    private String challengeSessionLink;
    private String defaultSubjectEn;
    private String defaultSubjectAr;
    private Challenges challenge;
    private String inviteLink;
    private User user;
}
