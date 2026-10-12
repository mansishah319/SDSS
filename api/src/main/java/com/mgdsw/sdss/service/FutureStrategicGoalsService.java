/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.strategic.goals.FutureStrategicGoalsRequest;
import com.mgdsw.sdss.dto.strategic.goals.FutureStrategicGoalsResponse;
import com.mgdsw.sdss.entity.strategic.FutureStrategicGoals;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Apr, 2024
 */
public interface FutureStrategicGoalsService extends BaseService<Integer, FutureStrategicGoals> {
    FutureStrategicGoalsResponse createNewFutureStrategicGoal(FutureStrategicGoalsRequest request);

    FutureStrategicGoalsResponse update(Integer strategicGoalsId, FutureStrategicGoalsRequest strategicGoalsRequest);
}
