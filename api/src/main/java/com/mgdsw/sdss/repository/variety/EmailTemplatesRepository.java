/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.variety;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.entity.variety.TemplateType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface EmailTemplatesRepository extends BaseRepository<EmailTemplates, Integer> {
    List<EmailTemplates> findByTemplateType(TemplateType templateType);

    EmailTemplates findByTemplateTypeAndIsDefaultTrue(TemplateType templateType);
}
