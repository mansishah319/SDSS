/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.email.dto;

import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.helper.template.TemplateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuartzEmailDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private String[] to;
    private String subject;
    private String content;
    private String templateName;
    private String attachmentPath;
    private String attachmentName;
    private String userDashboardUrl;
    private User user;
    private Challenges challenges;
    private TemplateDto templateDto;
}
