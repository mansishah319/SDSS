/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.variety;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.variety.Settings;
import com.mgdsw.sdss.repository.variety.SettingsRepository;
import com.mgdsw.sdss.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl extends AbstractBaseServiceImpl<Integer, Settings> implements SettingsService {

    private final SettingsRepository settingsRepository;

    @Override
    protected SettingsRepository repository() {
        return settingsRepository;
    }
}