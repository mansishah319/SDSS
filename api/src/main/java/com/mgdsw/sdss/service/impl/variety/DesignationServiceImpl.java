/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.variety;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.variety.Designation;
import com.mgdsw.sdss.repository.variety.DesignationRepository;
import com.mgdsw.sdss.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class DesignationServiceImpl extends AbstractBaseServiceImpl<Integer, Designation> implements DesignationService {

    private final DesignationRepository designationRepository;

    @Override
    protected DesignationRepository repository() {
        return designationRepository;
    }
}