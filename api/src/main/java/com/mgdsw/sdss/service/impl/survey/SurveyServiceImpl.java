/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.survey;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.survey.SurveyCreationRequest;
import com.mgdsw.sdss.dto.survey.SurveyForSolutionResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.entity.survey.Survey;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.repository.survey.SurveyRepository;
import com.mgdsw.sdss.service.ChallengesService;
import com.mgdsw.sdss.service.SolutionService;
import com.mgdsw.sdss.service.SurveyService;
import com.mgdsw.sdss.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class SurveyServiceImpl extends AbstractBaseServiceImpl<Integer, Survey> implements SurveyService {

    private final SurveyRepository surveyRepository;

    private final ChallengesService challengesService;
    private final SolutionService solutionService;
    private final UserService userService;

    @Override
    protected SurveyRepository repository() {
        return surveyRepository;
    }

    @Override
    @Transactional
    public SurveyForSolutionResponse createAndUpdate(SurveyCreationRequest request) {
        // if existed, update else creates
        Optional<Survey> surveyBySolutionForTheCurrentUser = surveyBySolutionForTheCurrentUser(request.getSolutionId());
        if (surveyBySolutionForTheCurrentUser.isPresent()) {
            Survey survey = surveyBySolutionForTheCurrentUser.get();
            return this.updateUserSurvey(request, survey.getId());
        }
        Survey survey = this.applySurveyRelations(super.modelMapper.map(request, Survey.class), request);
        log.info("Survey Saved Successfully With ID is --> {}", survey.getId());
        return super.modelMapper.map(survey, SurveyForSolutionResponse.class);
    }

    @Override
    @Transactional
    public SurveyForSolutionResponse update(SurveyCreationRequest request, Integer surveyId) {
        return updateUserSurvey(request, surveyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurveyForSolutionResponse> findAllBySolutionId(Integer solutionId) {
        return surveyRepository.findBySolution(this.retrieveSolution(solutionId)).stream().map(survey ->
                modelMapper.map(survey, SurveyForSolutionResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyForSolutionResponse findSurveyBySolutionId(Integer solutionId) {
        return surveyBySolutionForTheCurrentUser(solutionId).map(survey ->
                modelMapper.map(survey, SurveyForSolutionResponse.class)).orElse(null);
    }

    private Optional<Survey> surveyBySolutionForTheCurrentUser(Integer solutionId) {
        return surveyRepository.findBySolutionAndUser(this.retrieveSolution(solutionId), this.retrieveCurrentUser());
    }

    private SurveyForSolutionResponse updateUserSurvey(SurveyCreationRequest request, Integer surveyId) {
        Survey survey = super.findById(surveyId);
        Survey updatedSurvey = this.applySurveyRelations(survey, request);
        log.info("User Survey Updated Successfully For ID --> {}", updatedSurvey.getId());
        return super.modelMapper.map(updatedSurvey, SurveyForSolutionResponse.class);
    }

    private Survey applySurveyRelations(Survey survey, SurveyCreationRequest request) {
        modelMapper.map(request, survey);
        survey.setId(survey.getId());
        survey.setUser(this.retrieveCurrentUser());
        survey.setSolution(this.retrieveSolution(request.getSolutionId()));
        survey.setChallenges(this.retrieveChallenge(request.getChallengeId()));
        return surveyRepository.save(survey);
    }

    private User retrieveCurrentUser() {
        return userService.retrieveCurrentUser();
    }

    private Challenges retrieveChallenge(int challengeId) {
        return challengesService.findById(challengeId);
    }

    private Solution retrieveSolution(int solutionId) {
        return solutionService.findById(solutionId);
    }
}