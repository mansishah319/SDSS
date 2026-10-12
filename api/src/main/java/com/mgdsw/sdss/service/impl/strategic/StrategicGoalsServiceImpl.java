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
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsRequest;
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsResponse;
import com.mgdsw.sdss.entity.strategic.StrategicGoals;
import com.mgdsw.sdss.repository.strategic.StrategicGoalsRepository;
import com.mgdsw.sdss.service.StrategicGoalDirectionService;
import com.mgdsw.sdss.service.StrategicGoalsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategicGoalsServiceImpl extends AbstractBaseServiceImpl<Integer, StrategicGoals> implements StrategicGoalsService {

    private final StrategicGoalsRepository strategicGoalsRepository;
    private final StrategicGoalDirectionService strategicGoalDirectionService;

    @Override
    protected StrategicGoalsRepository repository() {
        return strategicGoalsRepository;
    }

    @Override
    @Transactional
    public StrategicGoalsResponse createNewStrategicGoal(StrategicGoalsRequest request) {
        StrategicGoals strategicGoal = super.create(request);
        strategicGoalDirectionService.create(strategicGoal, request.getStrategicDirectionIds());
        return super.create(strategicGoal, StrategicGoalsResponse.class);
    }

    @Override
    public StrategicGoalsResponse getById(Integer strategicGoalId) {
        StrategicGoalsResponse strategicGoalsResponse = super.findById(strategicGoalId, StrategicGoalsResponse.class);
        return prepareResponseWithAllDirectionsResponse(strategicGoalsResponse);
    }

    @Override
    @Transactional
    public StrategicGoalsResponse update(Integer strategicGoalsId, StrategicGoalsRequest strategicGoalsRequest) {
        StrategicGoals updateStrategicGoals = super.update(strategicGoalsRequest, strategicGoalsId);
        List<Integer> strategicDirectionIds = strategicGoalsRequest.getStrategicDirectionIds();
        log.info("strategicDirectionIds selected to Update --> {}", strategicDirectionIds);
        if (Objects.nonNull(strategicDirectionIds) && !strategicDirectionIds.isEmpty())
            strategicGoalDirectionService.updateDataForGoalId(updateStrategicGoals, strategicDirectionIds);
        return super.modelMapper.map(updateStrategicGoals, StrategicGoalsResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StrategicGoalsResponse> retrieveAllGoalsByIds(List<Integer> allGoalsIdsByDirectionId) {
        return strategicGoalsRepository.findAllByIdIn(allGoalsIdsByDirectionId).stream().map(strategicGoal ->
                super.modelMapper.map(strategicGoal, StrategicGoalsResponse.class)).collect(Collectors.toList());
    }

    @Override
    public PaginationResponse<StrategicGoalsResponse> getAll(Pageable pageable) {
        PaginationResponse<StrategicGoalsResponse> response = super.getAllWithPagination(pageable, StrategicGoalsResponse.class);
        response.getData().forEach(this::prepareResponseWithAllDirectionsResponse);
        return response;
    }

    private StrategicGoalsResponse prepareResponseWithAllDirectionsResponse(StrategicGoalsResponse strategicGoalsResponse) {
        strategicGoalsResponse.setStrategicDirectionIds(this.prepareResponseWithDirectionData(strategicGoalsResponse.getId()));
        strategicGoalsResponse.setStrategicDirectionResponseList(this.prepareResponseWithAllDirectionsResponse(strategicGoalsResponse.getId()));
        return strategicGoalsResponse;
    }

    private List<Integer> prepareResponseWithDirectionData(Integer strategicGoalId) {
        return strategicGoalDirectionService.findAllDirectionIdsByGoalId(strategicGoalId);
    }

    private List<StrategicDirectionResponse> prepareResponseWithAllDirectionsResponse(Integer strategicGoalId) {
        return strategicGoalDirectionService.findAllDirectionsByGoalId(strategicGoalId);
    }
}