/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.challenge.VotingResultPerStageResponse;
import com.mgdsw.sdss.dto.solution.SolutionResponse;
import com.mgdsw.sdss.dto.voting.CreateVotingRequest;
import com.mgdsw.sdss.dto.voting.VotingResponse;
import com.mgdsw.sdss.dto.voting.VotingResultResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.entity.variety.Voting;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface VotingService extends BaseService<Integer, Voting> {

    VotingResponse create(CreateVotingRequest request);

    List<VotingResponse> findBySolutionId(Integer solutionId);

    List<VotingResponse> findByChallengeId(Integer challengeId);

    VotingResultResponse retrieveSolutionVotingResult(Integer challengeId);

    SolutionResponse removeCurrentUserVote(Integer solutionId);

    boolean checkTheCurrentUserIsVoted(Integer solutionId);

    long countTheNumberOfVotingForTheCurrentUser();

    long countTheNumberOfNotVotedForTheCurrentUser();

    void removeAllByChallenge(Challenges challengeNeedToRemove);

    List<VotingResultPerStageResponse> retrieveSolutionVotingResultPerStage(Integer challengeId);

    VotingResultPerStageResponse retrieveSolutionVotingResultPerStage(Challenges challenge, Integer stageNumber);

    void updateVotingRoundLevel(Challenges challenges, Solution solution, Integer currentRoundLevel);
}
