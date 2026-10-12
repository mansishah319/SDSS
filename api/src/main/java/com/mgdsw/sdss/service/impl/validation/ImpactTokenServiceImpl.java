/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.validation;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.validation.ImpactToken;
import com.mgdsw.sdss.repository.validation.ImpactTokenRepository;
import com.mgdsw.sdss.service.ImpactTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class ImpactTokenServiceImpl extends AbstractBaseServiceImpl<Integer, ImpactToken> implements ImpactTokenService {

    private final ImpactTokenRepository impactTokenRepository;

    @Override
    protected ImpactTokenRepository repository() {
        return impactTokenRepository;
    }
}