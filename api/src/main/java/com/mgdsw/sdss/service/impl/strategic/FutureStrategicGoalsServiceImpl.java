/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.strategic;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.strategic.goals.FutureStrategicGoalsRequest;
import com.mgdsw.sdss.dto.strategic.goals.FutureStrategicGoalsResponse;
import com.mgdsw.sdss.entity.strategic.FutureStrategicGoals;
import com.mgdsw.sdss.repository.strategic.FutureStrategicGoalsRepository;
import com.mgdsw.sdss.service.FutureStrategicGoalsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Apr, 2024
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FutureStrategicGoalsServiceImpl extends AbstractBaseServiceImpl<Integer, FutureStrategicGoals> implements FutureStrategicGoalsService {

    private final FutureStrategicGoalsRepository futureStrategicGoalsRepository;

    @Override
    protected FutureStrategicGoalsRepository repository() {
        return futureStrategicGoalsRepository;
    }

    @Override
    @Transactional
    public FutureStrategicGoalsResponse createNewFutureStrategicGoal(FutureStrategicGoalsRequest request) {
        FutureStrategicGoals futureStrategicGoals = super.create(request);
        return super.create(futureStrategicGoals, FutureStrategicGoalsResponse.class);
    }

    @Override
    @Transactional
    public FutureStrategicGoalsResponse update(Integer strategicGoalsId, FutureStrategicGoalsRequest strategicGoalsRequest) {
        FutureStrategicGoals updateStrategicGoals = super.update(strategicGoalsRequest, strategicGoalsId);
        return super.modelMapper.map(updateStrategicGoals, FutureStrategicGoalsResponse.class);
    }
}
