/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.solution.MergeSolutionByStageRequest;
import com.mgdsw.sdss.dto.solution.SolutionCreationRequest;
import com.mgdsw.sdss.dto.solution.SolutionFullResponse;
import com.mgdsw.sdss.dto.solution.SolutionResponse;
import com.mgdsw.sdss.dto.solution.SolutionsPerStageResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.enums.Operations;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface SolutionService extends BaseService<Integer, Solution> {

    void createSolutionForSpecificUserAndSolution(List<String> solutionTexts, Challenges challenges, Operations operations);

    SolutionResponse create(SolutionCreationRequest request);

    List<SolutionFullResponse> retrieveAllSolutionByChallengeId(Integer challengeId, Integer roundLevel);

    List<SolutionFullResponse> updateRoundLevelByChallengeIdAndSelectedSolutions(Integer roundLevel, Integer challengeId, Integer isFinal, List<Integer> solutionsListIds);

    void updateSolutionWithTotalVoteCount(Integer solutionId, long voteCount);

    List<SolutionResponse> findThFinalSolutionsByChallengeId(Integer challengeId);

    void removeAllByChallenge(Challenges challengeNeedToRemove);

    List<SolutionsPerStageResponse> retrieveAllSolutionByChallengePerStage(Challenges challenge);

    SolutionResponse mergeSolutions(MergeSolutionByStageRequest request);
}
