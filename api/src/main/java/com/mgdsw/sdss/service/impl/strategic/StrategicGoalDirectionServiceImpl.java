/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.strategic;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.dto.strategic.directions.StrategicDirectionResponse;
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsResponse;
import com.mgdsw.sdss.entity.strategic.StrategicGoalDirection;
import com.mgdsw.sdss.entity.strategic.StrategicGoals;
import com.mgdsw.sdss.repository.strategic.StrategicGoalDirectionRepository;
import com.mgdsw.sdss.service.StrategicDirectionService;
import com.mgdsw.sdss.service.StrategicGoalDirectionService;
import com.mgdsw.sdss.service.StrategicGoalsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.utils.TranslatorUtil.ClassFiledNames.StrategicDirection.STRATEGIC_DIRECTION_TITLE;
import static com.mgdsw.sdss.common.utils.TranslatorUtil.Message.NOT_FOUND;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
public class StrategicGoalDirectionServiceImpl extends AbstractBaseServiceImpl<Integer, StrategicGoalDirection> implements StrategicGoalDirectionService {

    private final StrategicGoalDirectionRepository strategicGoalDirectionRepository;
    private final StrategicDirectionService strategicDirectionService;
    private final StrategicGoalsService strategicGoalsService;

    public StrategicGoalDirectionServiceImpl(StrategicGoalDirectionRepository strategicGoalDirectionRepository,
                                             @Lazy StrategicDirectionService strategicDirectionService,
                                             @Lazy StrategicGoalsService strategicGoalsService) {
        this.strategicGoalDirectionRepository = strategicGoalDirectionRepository;
        this.strategicDirectionService = strategicDirectionService;
        this.strategicGoalsService = strategicGoalsService;
    }

    @Override
    protected StrategicGoalDirectionRepository repository() {
        return strategicGoalDirectionRepository;
    }

    @Override
    @Transactional
    public void create(StrategicGoals strategicGoal, List<Integer> strategicDirectionIds) {
        this.createDirectionGoal(strategicGoal, strategicDirectionIds);
    }

    @Override
    public List<Integer> findAllDirectionIdsByGoalId(Integer goalId) {
        return strategicGoalDirectionRepository.findAllByStrategicGoalId(goalId)
                .stream().map(StrategicGoalDirection::getCategoryId).collect(Collectors.toList());
    }

    @Override
    public List<StrategicDirectionResponse> findAllDirectionsByGoalId(Integer goalId) {
        return strategicDirectionService.findAllInTheseIds(findAllDirectionIdsByGoalId(goalId));
    }

    @Override
    @Transactional
    public void updateDataForGoalId(StrategicGoals strategicGoal, List<Integer> strategicDirectionIds) {
        strategicGoalDirectionRepository.deleteAllByStrategicGoalId(strategicGoal.getId());
        this.createDirectionGoal(strategicGoal, strategicDirectionIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StrategicGoalsResponse> retrieveAllStrategicGoalsByDirectionId(Integer strategicDirectionId) {
        return strategicGoalsService.retrieveAllGoalsByIds(this.findAllGoalsIdsByDirectionIdPrivate(strategicDirectionId));
    }

    public List<Integer> findAllGoalsIdsByDirectionIdPrivate(Integer directionId) {
        return strategicGoalDirectionRepository.findAllByCategoryId(directionId)
                .stream().map(StrategicGoalDirection::getStrategicGoalId).collect(Collectors.toList());
    }

    private void checkStrategicDirectionExist(Integer strategicDirectionId) {
        if (!strategicDirectionService.findOptionalById(strategicDirectionId).isPresent()) {
            throw new CustomDataException(
                    super.translator.getMessage(NOT_FOUND, Arrays.asList(super.translator.getMessage(STRATEGIC_DIRECTION_TITLE), strategicDirectionId)),
                    ErrorCodes.NOT_FOUND_ERR.getErrorCode());
        }
    }

    private void createDirectionGoal(StrategicGoals strategicGoal, List<Integer> strategicDirectionIds) {
        strategicDirectionIds.forEach(directionId -> {
                    checkStrategicDirectionExist(directionId);
                    strategicGoalDirectionRepository.save(StrategicGoalDirection.builder()
                            .strategicGoalId(strategicGoal.getId())
                            .categoryId(directionId)
                            .build());
                }
        );
    }
}