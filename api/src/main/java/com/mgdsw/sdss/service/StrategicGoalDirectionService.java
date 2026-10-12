/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.strategic.directions.StrategicDirectionResponse;
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsResponse;
import com.mgdsw.sdss.entity.strategic.StrategicGoalDirection;
import com.mgdsw.sdss.entity.strategic.StrategicGoals;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface StrategicGoalDirectionService extends BaseService<Integer, StrategicGoalDirection> {

    void create(StrategicGoals strategicGoal, List<Integer> strategicDirectionIds);

    List<Integer> findAllDirectionIdsByGoalId(Integer goalId);

    List<StrategicDirectionResponse> findAllDirectionsByGoalId(Integer goalId);

    void updateDataForGoalId(StrategicGoals strategicGoal, List<Integer> strategicDirectionIds);

    List<StrategicGoalsResponse> retrieveAllStrategicGoalsByDirectionId(Integer strategicDirectionId);
}
