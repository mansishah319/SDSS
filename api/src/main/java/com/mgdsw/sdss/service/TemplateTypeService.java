/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.template.type.TemplateKeysResponse;
import com.mgdsw.sdss.dto.template.type.TemplateTypeRequest;
import com.mgdsw.sdss.dto.template.type.TemplateTypeResponse;
import com.mgdsw.sdss.entity.variety.TemplateType;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */
public interface TemplateTypeService extends BaseService<Integer, TemplateType> {

    TemplateTypeResponse createTemplateType(TemplateTypeRequest request);

    TemplateTypeResponse updateTemplateType(Integer templateTypeId, TemplateTypeRequest request);

    TemplateType findByTemplateTypeKey(String templateTypeKey);

    TemplateType findByTemplateTypeAcceptNull(String templateTypeKey);

    List<TemplateKeysResponse> getTemplateKeys();
}
