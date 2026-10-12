/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.strategic;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.strategic.FutureStrategicDirection;
import com.mgdsw.sdss.repository.strategic.FutureStrategicDirectionRepository;
import com.mgdsw.sdss.service.FutureStrategicDirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Apr, 2024
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FutureStrategicDirectionServiceImpl extends AbstractBaseServiceImpl<Integer, FutureStrategicDirection> implements FutureStrategicDirectionService {

    private final FutureStrategicDirectionRepository futureStrategicDirectionRepository;

    @Override
    protected FutureStrategicDirectionRepository repository() {
        return futureStrategicDirectionRepository;
    }
}
