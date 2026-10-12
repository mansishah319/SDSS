/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.strategic;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.strategic.StrategicGoalDirection;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface StrategicGoalDirectionRepository extends BaseRepository<StrategicGoalDirection, Integer> {

    List<StrategicGoalDirection> findAllByStrategicGoalId(int strategicGoalId);

    List<StrategicGoalDirection> findAllByCategoryId(int directionGoalId);

    void deleteAllByStrategicGoalId(int strategicGoalId);
}
