/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.solution;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.solution.MergeSolutionByStageRequest;
import com.mgdsw.sdss.dto.solution.SolutionCreationRequest;
import com.mgdsw.sdss.dto.solution.SolutionFullResponse;
import com.mgdsw.sdss.dto.solution.SolutionResponse;
import com.mgdsw.sdss.dto.solution.SolutionsPerStageResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.enums.ChallengeVisibility;
import com.mgdsw.sdss.enums.Operations;
import com.mgdsw.sdss.repository.solution.SolutionRepository;
import com.mgdsw.sdss.repository.survey.CommanderSurveyRepository;
import com.mgdsw.sdss.repository.survey.SolutionRatingProjection;
import com.mgdsw.sdss.repository.survey.SurveyRepository;
import com.mgdsw.sdss.repository.survey.ValidatorSurveyRepository;
import com.mgdsw.sdss.service.ChallengesService;
import com.mgdsw.sdss.service.SolutionService;
import com.mgdsw.sdss.service.UserService;
import com.mgdsw.sdss.service.VotingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
@Service
public class SolutionServiceImpl extends AbstractBaseServiceImpl<Integer, Solution> implements SolutionService {

    private final SolutionRepository solutionRepository;
    private final ChallengesService challengesService;
    private final VotingService votingService;
    private final UserService userService;
    private final SurveyRepository surveyRepository;
    private final ValidatorSurveyRepository validatorSurveyRepository;
    private final CommanderSurveyRepository commanderSurveyRepository;

    public SolutionServiceImpl(SolutionRepository solutionRepository,
                               @Lazy ChallengesService challengesService,
                               @Lazy VotingService votingService,
                               UserService userService,
                               SurveyRepository surveyRepository,
                               ValidatorSurveyRepository validatorSurveyRepository,
                               CommanderSurveyRepository commanderSurveyRepository) {
        this.solutionRepository = solutionRepository;
        this.challengesService = challengesService;
        this.userService = userService;
        this.votingService = votingService;
        this.surveyRepository = surveyRepository;
        this.validatorSurveyRepository = validatorSurveyRepository;
        this.commanderSurveyRepository = commanderSurveyRepository;
    }

    @Override
    protected SolutionRepository repository() {
        return solutionRepository;
    }

    @Override
    @Transactional
    public void createSolutionForSpecificUserAndSolution(List<String> solutionTexts, Challenges challenges, Operations operations) {
        if (operations.equals(Operations.UPDATE))
            solutionRepository.deleteByChallenges(challenges);
        solutionTexts.forEach(text ->
                solutionRepository.save(
                        this.saveNewSolution(
                                SolutionCreationRequest.builder()
                                        .solutionText(text)
                                        .queLevel(challenges.getQueLevel())
                                        .build(),
                                challenges, null)));
    }

    @Override
    @Transactional
    public SolutionResponse create(SolutionCreationRequest request) {
        return super.create(
                this.saveNewSolution(request, challengesService.findById(request.getChallengeId()), null),
                SolutionResponse.class
        );
    }

    @Override
    @Transactional
    public SolutionResponse mergeSolutions(MergeSolutionByStageRequest request) {
        return super.create(
                this.saveNewSolution(
                        SolutionCreationRequest.builder()
                                .challengeId(request.getChallengeId())
                                .queLevel(request.getQueLevel())
                                .solutionText(request.getNewSolutionText())
                                .build(),
                        challengesService.findById(request.getChallengeId()),
                        solutionRepository.findUserIdsBySolutionIds(request.getMergedSolutionIds())
                ),
                SolutionResponse.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolutionFullResponse> retrieveAllSolutionByChallengeId(Integer challengeId, Integer roundLevel) {
        Challenges challenges = retrieveChallengesById(challengeId);
        log.info("--> Current Challenge Round Level -> {}", challenges.getQueLevel());
        List<SolutionFullResponse> solutions = this.retrieveAllSolutionByChallengeAndStageNumber(challenges, roundLevel);
        this.enrichSolutionRatings(solutions);
        return solutions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolutionsPerStageResponse> retrieveAllSolutionByChallengePerStage(Challenges challenge) {
        List<Integer> stagesQueLevelByChallenges = challenge.getStageList();
        return stagesQueLevelByChallenges.stream().map(stageNumber ->
                SolutionsPerStageResponse.builder()
                        .solutionResponseList(this.retrieveAllSolutionByChallengeAndStageNumber(challenge, stageNumber))
                        .votingResultPerStageResponse(votingService.retrieveSolutionVotingResultPerStage(challenge, stageNumber))
                        .stageNumber(stageNumber)
                        .build()).collect(Collectors.toList()
        );
    }

    private List<SolutionFullResponse> retrieveAllSolutionByChallengeAndStageNumber(Challenges challenge, Integer roundLevel) {
        log.info("Current Challenge Round Level -> {}", challenge.getQueLevel());
        return Objects.isNull(roundLevel) ?
                super.mapList(solutionRepository.findAllByChallenges(challenge), SolutionFullResponse.class) :
                super.mapList(solutionRepository.findByChallengesAndStageLevel(challenge, roundLevel), SolutionFullResponse.class);
    }

    private void enrichSolutionRatings(List<SolutionFullResponse> solutions) {
        if (solutions == null || solutions.isEmpty()) {
            return;
        }

        List<Integer> solutionIds = solutions.stream()
                .map(SolutionFullResponse::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (solutionIds.isEmpty()) {
            return;
        }

        Map<Integer, SolutionRatingProjection> participantRatings = indexRatings(
                surveyRepository.aggregateRatingsBySolutionIds(solutionIds));
        Map<Integer, SolutionRatingProjection> validatorRatings = indexRatings(
                validatorSurveyRepository.aggregateRatingsBySolutionIds(solutionIds));
        Map<Integer, SolutionRatingProjection> commanderRatings = indexRatings(
                commanderSurveyRepository.aggregateRatingsBySolutionIds(solutionIds));

        solutions.forEach(solution -> applyRatingSummary(
                solution,
                participantRatings.get(solution.getId()),
                validatorRatings.get(solution.getId()),
                commanderRatings.get(solution.getId())
        ));
    }

    private Map<Integer, SolutionRatingProjection> indexRatings(List<SolutionRatingProjection> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, SolutionRatingProjection> indexedRatings = new HashMap<>();
        ratings.forEach(rating -> indexedRatings.put(rating.getSolutionId(), rating));
        return indexedRatings;
    }

    private void applyRatingSummary(SolutionFullResponse solution,
                                    SolutionRatingProjection participantRating,
                                    SolutionRatingProjection validatorRating,
                                    SolutionRatingProjection commanderRating) {
        long participantCount = ratingCount(participantRating);
        long validatorCount = ratingCount(validatorRating);
        long commanderCount = ratingCount(commanderRating);
        long totalCount = participantCount + validatorCount + commanderCount;
        double participantAverage = ratingAverage(participantRating);
        double validatorAverage = ratingAverage(validatorRating);
        double commanderAverage = ratingAverage(commanderRating);

        solution.setParticipantAverageRating(roundRating(participantAverage));
        solution.setParticipantRatingCount(participantCount);
        solution.setValidatorAverageRating(roundRating(validatorAverage));
        solution.setValidatorRatingCount(validatorCount);
        solution.setCommanderAverageRating(roundRating(commanderAverage));
        solution.setCommanderRatingCount(commanderCount);
        solution.setRatingCount(totalCount);
        solution.setAverageRating(totalCount == 0 ? 0D : roundRating(
                ((participantAverage * participantCount) +
                        (validatorAverage * validatorCount) +
                        (commanderAverage * commanderCount)) / totalCount));
    }

    private long ratingCount(SolutionRatingProjection rating) {
        return rating == null || rating.getRatingCount() == null ? 0L : rating.getRatingCount();
    }

    private double ratingAverage(SolutionRatingProjection rating) {
        return rating == null || rating.getAverageRating() == null ? 0D : rating.getAverageRating();
    }

    private double roundRating(double value) {
        return Math.round(value * 10D) / 10D;
    }

    @Override
    @Transactional
    public List<SolutionFullResponse> updateRoundLevelByChallengeIdAndSelectedSolutions(Integer roundLevel, Integer challengeId,
                                                                                        Integer isFinal, List<Integer> solutionsListIds) {
        Challenges challenges = retrieveChallengesById(challengeId);
        challengesService.updateChallengeRoundLevel(challenges, roundLevel, isFinal);
        if (Objects.nonNull(isFinal) && isFinal == 1) {
            // solutionRepository.updateQueLevelAndIsFinalForSolutions(solutionsListIds, Boolean.TRUE, challenges.getQueLevel())
            this.updateSolutionListBasedOnFinalStatus(solutionsListIds, challenges, Boolean.TRUE);
        } else {
            // solutionRepository.updateQueLevelForSolutions(solutionsListIds, challenges.getQueLevel())
            this.updateSolutionListBasedOnFinalStatus(solutionsListIds, challenges, Boolean.FALSE);
        }
        solutionsListIds.forEach(solutionId -> votingService.updateVotingRoundLevel(challenges, findById(solutionId), roundLevel));
        return solutionsListIds.stream().map(id -> super.findById(id, SolutionFullResponse.class)).collect(Collectors.toList());
    }

    private void updateSolutionListBasedOnFinalStatus(List<Integer> solutionsListIds, Challenges challenges, Boolean isFinal) {
        solutionsListIds.forEach(solutionId -> {
            Solution solution = this.findById(solutionId);
            int queLevel = challenges.getQueLevel();
            solution.setQueLevel(queLevel);
            solution.setIsFinal(isFinal);
            solution.getStageList().add(queLevel);
            solutionRepository.save(solution);
        });
    }

    @Override
    @Transactional
    public void updateSolutionWithTotalVoteCount(Integer solutionId, long voteCount) {
        log.info("Update Count for solutionId : {} with vote count is : {}", solutionId, voteCount);
        Solution solution = this.findById(solutionId);
        solution.setVoteCount(voteCount);
        solutionRepository.save(solution);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolutionResponse> findThFinalSolutionsByChallengeId(Integer challengeId) {
        return solutionRepository.findAllByChallengesAndIsFinal(challengesService.findById(challengeId), Boolean.TRUE)
                .stream().map(solution -> modelMapper.map(solution, SolutionResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeAllByChallenge(Challenges challengeNeedToRemove) {
        solutionRepository.deleteByChallenges(challengeNeedToRemove);
    }

    private Solution saveNewSolution(SolutionCreationRequest request, Challenges challenges, List<Integer> mergedSolutionUsers) {
        return Solution.builder()
                .solutionText(request.getSolutionText())
                .challenges(challenges)
                .user(Objects.nonNull(mergedSolutionUsers) ? null : userService.retrieveCurrentUser())
                .queLevel(request.getQueLevel())
                .mergedSolutionUsers(Objects.nonNull(mergedSolutionUsers) ? mergedSolutionUsers : null)
                .solutionColor(1)
                .initPublic(challenges.getVisibility().equals(ChallengeVisibility.PUBLIC) ? Boolean.TRUE : Boolean.FALSE)
                .voteCount(0)
                .voteColor(0)
                .voteColor(0)
                .point(0)
                .rank(0)
                .build();
    }

    private Challenges retrieveChallengesById(Integer challengeId) {
        return challengesService.findById(challengeId);
    }
}
