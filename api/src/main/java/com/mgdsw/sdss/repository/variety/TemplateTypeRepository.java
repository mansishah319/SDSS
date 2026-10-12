/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.variety;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.variety.TemplateType;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Repository
public interface TemplateTypeRepository extends BaseRepository<TemplateType, Integer> {

    Optional<TemplateType> findByTemplateTypeKey(TemplatesTypeKeys templateTypeKey);
}
