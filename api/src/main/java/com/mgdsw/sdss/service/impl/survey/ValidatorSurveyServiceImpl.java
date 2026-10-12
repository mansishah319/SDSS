/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.survey;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.survey.validator.ValidatorSurveyCreationRequest;
import com.mgdsw.sdss.dto.survey.validator.ValidatorSurveySolutionResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.entity.survey.ValidatorSurvey;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.repository.survey.ValidatorSurveyRepository;
import com.mgdsw.sdss.service.ChallengesService;
import com.mgdsw.sdss.service.SolutionService;
import com.mgdsw.sdss.service.UserService;
import com.mgdsw.sdss.service.ValidatorSurveyService;
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
public class ValidatorSurveyServiceImpl extends AbstractBaseServiceImpl<Integer, ValidatorSurvey> implements ValidatorSurveyService {

    private final ValidatorSurveyRepository validatorSurveyRepository;

    private final ChallengesService challengesService;
    private final SolutionService solutionService;
    private final UserService userService;

    @Override
    protected ValidatorSurveyRepository repository() {
        return validatorSurveyRepository;
    }

    @Override
    @Transactional
    public ValidatorSurveySolutionResponse create(ValidatorSurveyCreationRequest validatorSurveyCreationRequest) {
        // if exist update else create
        Optional<ValidatorSurvey> surveyBySolutionForTheCurrentUser = surveyBySolutionForTheCurrentUser(validatorSurveyCreationRequest.getSolutionId());
        if (surveyBySolutionForTheCurrentUser.isPresent()) {
            ValidatorSurvey survey = surveyBySolutionForTheCurrentUser.get();
            return this.updateValidatorSurvey(validatorSurveyCreationRequest, survey.getId());
        }
        ValidatorSurvey survey = this.applySurveyRelations(super.modelMapper.map(validatorSurveyCreationRequest, ValidatorSurvey.class), validatorSurveyCreationRequest);
        log.info("Survey Saved Successfully With ID is --> {}", survey.getId());
        return super.modelMapper.map(survey, ValidatorSurveySolutionResponse.class);
    }

    @Override
    @Transactional
    public ValidatorSurveySolutionResponse update(ValidatorSurveyCreationRequest request, Integer surveyId) {
        return updateValidatorSurvey(request, surveyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidatorSurveySolutionResponse> findAllBySolutionId(Integer solutionId) {
        return validatorSurveyRepository.findBySolution(this.retrieveSolution(solutionId)).stream().map(survey ->
                modelMapper.map(survey, ValidatorSurveySolutionResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ValidatorSurveySolutionResponse findSurveyBySolutionId(Integer solutionId) {
        return surveyBySolutionForTheCurrentUser(solutionId).map(survey ->
                modelMapper.map(survey, ValidatorSurveySolutionResponse.class)).orElse(null);
    }

    private Optional<ValidatorSurvey> surveyBySolutionForTheCurrentUser(Integer solutionId) {
        return validatorSurveyRepository.findBySolutionAndUser(this.retrieveSolution(solutionId), this.retrieveCurrentUser());
    }

    private ValidatorSurvey applySurveyRelations(ValidatorSurvey validatorSurvey, ValidatorSurveyCreationRequest validatorSurveyCreationRequest) {
        modelMapper.map(validatorSurveyCreationRequest, validatorSurvey);
        validatorSurvey.setId(validatorSurvey.getId());
        validatorSurvey.setChallenges(this.retrieveChallenge(validatorSurveyCreationRequest.getChallengeId()));
        validatorSurvey.setSolution(this.retrieveSolution(validatorSurveyCreationRequest.getSolutionId()));
        validatorSurvey.setUser(this.retrieveCurrentUser());
        return validatorSurveyRepository.save(validatorSurvey);
    }

    private ValidatorSurveySolutionResponse updateValidatorSurvey(ValidatorSurveyCreationRequest validatorSurveyCreationRequest, Integer surveyId) {
        ValidatorSurvey survey = super.findById(surveyId);
        ValidatorSurvey updatedValidatorSurvey = this.applySurveyRelations(survey, validatorSurveyCreationRequest);
        log.info("Validator Survey Updated Successfully For ID --> {}", updatedValidatorSurvey.getId());
        return super.modelMapper.map(updatedValidatorSurvey, ValidatorSurveySolutionResponse.class);
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
