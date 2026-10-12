/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.variety;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.dto.challenge.VotingResultPerStageResponse;
import com.mgdsw.sdss.dto.solution.SolutionResponse;
import com.mgdsw.sdss.dto.voting.CreateVotingRequest;
import com.mgdsw.sdss.dto.voting.VotingResponse;
import com.mgdsw.sdss.dto.voting.VotingResultResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.variety.Voting;
import com.mgdsw.sdss.repository.variety.VotingRepository;
import com.mgdsw.sdss.service.ChallengesService;
import com.mgdsw.sdss.service.SolutionService;
import com.mgdsw.sdss.service.UserInvitationService;
import com.mgdsw.sdss.service.UserService;
import com.mgdsw.sdss.service.VotingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.utils.FieldNamesUtil.VOTE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
@Service
public class VotingServiceImpl extends AbstractBaseServiceImpl<Integer, Voting> implements VotingService {

    private final VotingRepository votingRepository;
    private final UserService userService;
    private final SolutionService solutionService;
    private final ChallengesService challengesService;
    private final UserInvitationService userInvitationService;

    public VotingServiceImpl(VotingRepository votingRepository,
                             UserService userService,
                             SolutionService solutionService,
                             @Lazy ChallengesService challengesService,
                             UserInvitationService userInvitationService) {
        this.votingRepository = votingRepository;
        this.userService = userService;
        this.solutionService = solutionService;
        this.challengesService = challengesService;
        this.userInvitationService = userInvitationService;
    }

    @Override
    protected VotingRepository repository() {
        return votingRepository;
    }

    @Override
    @Transactional
    public VotingResponse create(CreateVotingRequest request) {
        int solutionId = request.getSolutionId();
        this.checkExistenceBySolutionsAndUser(solutionId);
        Solution solution = this.retrieveSolutionById(solutionId);
        Voting savedVote = votingRepository.save(Voting.builder()
                .challenges(this.retrieveChallengeById(request.getChallengeId()))
                .solution(solution)
                .user(this.retrieveCurrentUser())
                .queLevel(request.getQueLevel())
                .build());
        solutionService.updateSolutionWithTotalVoteCount(solutionId, votingRepository.countBySolution(solution));
        return super.create(savedVote, VotingResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VotingResponse> findBySolutionId(Integer solutionId) {
        return votingRepository.findBySolution(solutionService.findById(solutionId)).stream().map(vote ->
                modelMapper.map(vote, VotingResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VotingResponse> findByChallengeId(Integer challengeId) {
        return this.retrieveAllVotesByChallengeId(challengeId).stream().map(vote ->
                modelMapper.map(vote, VotingResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VotingResultResponse retrieveSolutionVotingResult(Integer challengeId) {
        Map<String, Long> solutionVotingResult = this.retrieveAllVotesByChallengeId(challengeId).stream().collect(Collectors.groupingBy(
                vote -> String.valueOf(vote.getSolution().getId()),
                Collectors.counting()
        ));
        return VotingResultResponse.builder()
                .solutionVotingResult(solutionVotingResult)
                .solutionTextList(new ArrayList<>(solutionVotingResult.keySet()))
                .countOfVotes(new ArrayList<>(solutionVotingResult.values()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VotingResultPerStageResponse> retrieveSolutionVotingResultPerStage(Integer challengeId) {
        Challenges challenge = retrieveChallengeById(challengeId);
        List<Integer> stagesQueLevelByChallenges = votingRepository.findQueLevelByChallenges(challenge);
        return stagesQueLevelByChallenges.stream().map(stage -> {
            Map<String, Long> solutionVotingResult = this.retrieveAllVotesByChallengeId(challenge, stage).stream().collect(Collectors.groupingBy(
                    vote -> vote.getSolution().getSolutionText(),
                    Collectors.counting()
            ));
            return VotingResultPerStageResponse.builder()
                    .votingResultResponse(
                            VotingResultResponse.builder()
                                    .solutionVotingResult(solutionVotingResult)
                                    .solutionTextList(new ArrayList<>(solutionVotingResult.keySet()))
                                    .countOfVotes(new ArrayList<>(solutionVotingResult.values()))
                                    .build()
                    )
                    .stageNumber(stage)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VotingResultPerStageResponse retrieveSolutionVotingResultPerStage(Challenges challenge, Integer stageNumber) {
        Map<String, Long> solutionVotingResult = this.retrieveAllVotesByChallengeId(challenge, stageNumber).stream().collect(Collectors.groupingBy(
                vote -> String.valueOf(vote.getSolution().getId()),
                Collectors.counting()
        ));
        return VotingResultPerStageResponse.builder()
                .votingResultResponse(
                        VotingResultResponse.builder()
                                .solutionVotingResult(solutionVotingResult)
                                .solutionTextList(new ArrayList<>(solutionVotingResult.keySet()))
                                .countOfVotes(new ArrayList<>(solutionVotingResult.values()))
                                .build()
                ).build();

    }

    @Override
    @Transactional
    public SolutionResponse removeCurrentUserVote(Integer solutionId) {
        Solution solution = this.retrieveSolutionById(solutionId);
        Long result = votingRepository.deleteBySolutionAndUser(solution, this.retrieveCurrentUser());
        log.info("result is {}", result);
        solutionService.updateSolutionWithTotalVoteCount(solutionId, votingRepository.countBySolution(solution));
        return modelMapper.map(solution, SolutionResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkTheCurrentUserIsVoted(Integer solutionId) {
        return votingRepository.existsBySolutionAndUser(this.retrieveSolutionById(solutionId), this.retrieveCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public long countTheNumberOfVotingForTheCurrentUser() {
        return this.calcTheNumberOfVotingForTheCurrentUser();
    }

    @Override
    @Transactional(readOnly = true)
    public long countTheNumberOfNotVotedForTheCurrentUser() {
        return Math.abs(this.retrieveChallengesByCurrentUserInvited().size() - this.calcTheNumberOfVotingForTheCurrentUser());
    }

    @Override
    public void removeAllByChallenge(Challenges challengeNeedToRemove) {
        votingRepository.deleteAllByChallenges(challengeNeedToRemove);
    }

    @Override
    @Transactional
    public void updateVotingRoundLevel(Challenges challenges, Solution solution, Integer updatedRoundLevel) {
        List<Voting> byChallengesAndSolutionAndQueLevel =
                votingRepository.findByChallengesAndSolution(challenges, solution);
        byChallengesAndSolutionAndQueLevel.forEach(vote -> {
            log.info("Voting Need to update ID is : {}", vote.getId());
            vote.setQueLevel(updatedRoundLevel);
            votingRepository.save(vote);
        });
    }

    private long calcTheNumberOfVotingForTheCurrentUser() {
        return votingRepository.countByChallengesIn(this.retrieveChallengesByCurrentUserInvited());
    }

    private List<Challenges> retrieveChallengesByCurrentUserInvited() {
        return userInvitationService.findChallengesByUser(userService.retrieveCurrentUser());
    }

    private void checkExistenceBySolutionsAndUser(Integer solutionId) {
        if (votingRepository.existsBySolutionAndUser(this.retrieveSolutionById(solutionId), this.retrieveCurrentUser())) {
            throw new CustomDataException(String.format(ErrorCodes.ALREADY_EXIST.getMessage(), VOTE), ErrorCodes.ALREADY_EXIST.getErrorCode());
        }
    }

    private User retrieveCurrentUser() {
        return userService.retrieveCurrentUser();
    }

    private Challenges retrieveChallengeById(Integer challengeId) {
        return challengesService.findById(challengeId);
    }

    private Solution retrieveSolutionById(Integer solutionId) {
        return solutionService.findById(solutionId);
    }

    private List<Voting> retrieveAllVotesByChallengeId(Integer challengeId) {
        return votingRepository.findByChallenges(this.retrieveChallengeById(challengeId));
    }

    private List<Voting> retrieveAllVotesByChallengeId(Challenges challenge, Integer queLevel) {
        return votingRepository.findByChallengesAndQueLevel(challenge, queLevel);
    }
}
