/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.strategic;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.dto.strategic.directions.StrategicDirectionResponse;
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsResponse;
import com.mgdsw.sdss.entity.strategic.StrategicDirection;
import com.mgdsw.sdss.repository.strategic.StrategicDirectionRepository;
import com.mgdsw.sdss.service.StrategicDirectionService;
import com.mgdsw.sdss.service.StrategicGoalDirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class StrategicDirectionServiceImpl extends AbstractBaseServiceImpl<Integer, StrategicDirection> implements StrategicDirectionService {

    private final StrategicDirectionRepository strategicDirectionRepository;
    private final StrategicGoalDirectionService strategicGoalDirectionService;

    @Override
    protected StrategicDirectionRepository repository() {
        return strategicDirectionRepository;
    }

    @Override
    public PaginationResponse<StrategicDirectionResponse> getAll(Pageable pageable) {
        try {
            return super.getAllWithPagination(pageable, StrategicDirectionResponse.class);
        } catch (Exception e) {
            log.error("Failed to retrieve strategic direction list", e);
            return PaginationResponse.<StrategicDirectionResponse>builder()
                    .totalElements(0L)
                    .data(Collections.emptyList())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<StrategicDirectionResponse> retrieveDropdownList() {
        return super.getAll(StrategicDirectionResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StrategicDirectionResponse> findAllInTheseIds(List<Integer> directionIds) {
        return strategicDirectionRepository.findAllByIdIn(directionIds).stream().map(
                direction -> super.modelMapper.map(direction, StrategicDirectionResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StrategicGoalsResponse> retrieveAllStrategicGoalsByDirectionId(Integer strategicDirectionId) {
        return strategicGoalDirectionService.retrieveAllStrategicGoalsByDirectionId(strategicDirectionId);
    }
}