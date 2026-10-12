/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.validation;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.validation.PasswordResets;
import com.mgdsw.sdss.repository.validation.PasswordResetsRepository;
import com.mgdsw.sdss.service.PasswordResetsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class PasswordResetsServiceImpl extends AbstractBaseServiceImpl<Integer, PasswordResets> implements PasswordResetsService {

    private final PasswordResetsRepository passwordResetsRepository;

    @Override
    protected PasswordResetsRepository repository() {
        return passwordResetsRepository;
    }
}