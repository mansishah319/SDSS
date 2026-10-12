/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.template.EmailTemplatesRequest;
import com.mgdsw.sdss.dto.template.EmailTemplatesResponse;
import com.mgdsw.sdss.entity.variety.EmailTemplates;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface EmailTemplatesService extends BaseService<Integer, EmailTemplates> {

    EmailTemplatesResponse createNewEmailTemplate(EmailTemplatesRequest request);

    EmailTemplatesResponse updateEmailTemplate(EmailTemplatesRequest request, Integer templateId);

    EmailTemplates retrieveActiveEmailTemplatesByTemplateType(String templateKey);
}
