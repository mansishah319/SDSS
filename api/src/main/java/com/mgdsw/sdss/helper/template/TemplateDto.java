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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 9月, 2024
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String subjectEn;
    private String subjectAr;
    private String contentEn;
    private String contentAr;
    private String path;
    private User user;
    private Challenges challenge;
    private String inviteLink;
    private String[] emailList;
    private boolean isLanguageEnglish;
    private String challengeSessionLink;
}
