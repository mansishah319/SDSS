/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.dto.strategic.directions.StrategicDirectionResponse;
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsResponse;
import com.mgdsw.sdss.entity.strategic.StrategicDirection;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface StrategicDirectionService extends BaseService<Integer, StrategicDirection> {

    PaginationResponse<StrategicDirectionResponse> getAll(Pageable pageable);

    List<StrategicDirectionResponse> retrieveDropdownList();

    List<StrategicDirectionResponse> findAllInTheseIds(List<Integer> directionIds);

    List<StrategicGoalsResponse> retrieveAllStrategicGoalsByDirectionId(Integer strategicDirectionId);
}
