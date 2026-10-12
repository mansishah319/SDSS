/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAccessDeniedException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomValidationException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.UpdateSdssSurveyStatus;
import com.mgdsw.sdss.dto.surveys.round.RoundReportMainResponse;
import com.mgdsw.sdss.dto.surveys.round.SdssSurveyRoundRequest;
import com.mgdsw.sdss.dto.surveys.round.SdssSurveyRoundResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.SdssStatus;
import com.mgdsw.sdss.mapper.SdssSurveyRoundMapper;
import com.mgdsw.sdss.repository.surveys.SdssSurveyRoundRepository;
import com.mgdsw.sdss.service.SdssSurveyInvitationService;
import com.mgdsw.sdss.service.SdssSurveyRoundService;
import com.mgdsw.sdss.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.COMMA_SPACE;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_TITLE_AR;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_TITLE_EN;
import static com.mgdsw.sdss.enums.TemplatesTypeKeys.SURVEY_INVITATION_KEY;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class SdssSurveyRoundServiceImpl extends AbstractBaseServiceImpl<Integer, SdssSurveyRound> implements SdssSurveyRoundService {

    private final SdssSurveyRoundRepository repository;

    private final SdssSurveyInvitationService surveyInvitationService;
    private final SdssSurveyRoundMapper mapper;
    private final UserService userService;

    @Override
    protected SdssSurveyRoundRepository repository() {
        return repository;
    }

    @Override
    @Transactional
    public SdssSurveyRoundResponse create(SdssSurveyRoundRequest request, SdssSurvey survey) {
        checkIfSurveysMoreThanFiveRoundsForRound(request.getRoundNumber());
        super.checkIsTheCurrentUserAllowedToDoThat(survey.getCreatedBy());
        this.updateTitleForSingleCreation(request);
        this.checkUniquenessBySurveyAndRoundNumber(request.getRoundNumber(), survey);
        SdssSurveyRound round = this.createProcess(request, survey);
        surveyInvitationService.initInvitation(SURVEY_INVITATION_KEY, survey, round);
        return super.modelMapper.map(round, SdssSurveyRoundResponse.class);
    }

    @Override
    @Transactional
    public SdssSurveyRoundResponse update(SdssSurveyRoundRequest request, SdssSurvey survey, Integer roundId) {
        checkIfSurveysMoreThanFiveRoundsForRound(request.getRoundNumber());
        super.checkIsTheCurrentUserAllowedToDoThat(survey.getCreatedBy());
        this.updateTitleForSingleCreation(request);
        SdssSurveyRound surveyRound = super.findById(roundId);
        this.checkIfTheCurrentSurveyRoundNotActive(surveyRound);
        modelMapper.map(request, surveyRound);
        SdssSurveyRound round = mapper.mapToEntity(surveyRound, request, survey);
        round.setId(roundId);
        SdssSurveyRound sdssSurveyRound = repository.save(round);
        surveyInvitationService.initInvitation(SURVEY_INVITATION_KEY, survey, sdssSurveyRound);
        return super.modelMapper.map(sdssSurveyRound, SdssSurveyRoundResponse.class);
    }

    @Override
    @Transactional
    public List<SdssSurveyRound> createList(List<SdssSurveyRoundRequest> roundRequestList, SdssSurvey survey) {
        return roundRequestList.stream().map(roundReq -> {
            checkIfSurveysMoreThanFiveRoundsForRound(roundReq.getRoundNumber());
            return this.createProcess(roundReq, survey);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SdssSurveyRound updateStatusBySdssSurvey(UpdateSdssSurveyStatus updateSdssSurveyStatus, SdssSurvey sdssSurvey) {
        Integer roundNumber = updateSdssSurveyStatus.getRoundNumber();
        if (Boolean.TRUE.equals(sdssSurvey.getManualHandle()))
            super.checkIsTheCurrentUserAllowedToDoThat(sdssSurvey.getCreatedBy());
        SdssStatus sdssStatus = SdssStatus.fromString(updateSdssSurveyStatus.getRoundStatus());
        boolean updatedStatusIsActive = sdssStatus.equals(SdssStatus.IN_PROGRESS);
        SdssSurveyRound sdssSurveyRound = this.retrieveSurveyRound(roundNumber, sdssSurvey);
        if (updatedStatusIsActive) {
            this.checkIfTheCurrentRoundNeedToActiveAndThereAnotherIsActive(sdssSurvey);
        }
        sdssSurveyRound.setStatus(sdssStatus);
        sdssSurveyRound.setActive(updatedStatusIsActive);
        return repository.save(sdssSurveyRound);
    }

    @Override
    @Transactional(readOnly = true)
    public SdssSurveyRound findByRoundNumberAndSurvey(Integer roundNumber, SdssSurvey survey) {
        return this.retrieveSurveyRound(roundNumber, survey);
    }

    @Override
    @Transactional
    public void deleteAllBySurvey(SdssSurvey survey) {
        repository.deleteBySurvey(survey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurveyRound> findBySurvey(SdssSurvey survey) {
        return repository.findBySurvey(survey);
    }

    @Override
    public SdssSurveyRound findActiveRound(SdssSurvey survey) {
        return repository.findBySurveyAndActiveTrue(survey)
                .orElseThrow(
                        () -> new CustomNotFoundException(
                                LocalizedErrorCodes.NO_ROUND_ACTIVE_FOR_THIS_SURVEY.getLocalizedMessage(new Locale(DpCommonUtil.getUserCurrentPublicUserLocale())),
                                LocalizedErrorCodes.NO_ROUND_ACTIVE_FOR_THIS_SURVEY.getErrorCode()
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurveyRound> findCompletedRound(SdssSurvey survey) {
        return Optional.ofNullable(repository.findBySurveyAndStatus(survey, SdssStatus.COMPLETED)).orElse(new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoundReportMainResponse> findAllRoundsBasedOnStatus(List<SdssStatus> statusList) {
        User currentUser = userService.retrieveCurrentUser();
        if (Boolean.FALSE.equals(currentUser.getIsDelphiModerator()))
            throw new CustomAccessDeniedException(
                    LocalizedErrorCodes.USER_SURVEY_REPORT_ACCESS_DENIED.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                    LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode()
            );
        return repository.findByStatusesAndCreatedBy(statusList, String.valueOf(DpCommonUtil.retrieveCurrentUserGrpNumber()))
                .stream()
                .map(mapper::toRoundReportMainResponse)
                .sorted(Comparator.comparing(RoundReportMainResponse::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public void checkIfReportAllowedOrNot(SdssSurveyRound sdssSurveyRound) {
        if (!sdssSurveyRound.getStatus().equals(SdssStatus.COMPLETED))
            throw new CustomDataException(
                    String.format(
                            LocalizedErrorCodes.REPORT_SHOULD_PREPARED_AFTER_SURVEY_PROCESS_COMPLETED.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.REPORT_SHOULD_PREPARED_AFTER_SURVEY_PROCESS_COMPLETED.getErrorCode()
            );
    }

    private SdssSurveyRound createProcess(SdssSurveyRoundRequest request, SdssSurvey survey) {
        return repository.save(mapper.mapToEntity(modelMapper.map(request, SdssSurveyRound.class), request, survey));
    }

    private SdssSurveyRound retrieveSurveyRound(Integer roundNumber, SdssSurvey survey) {
        return repository.findByRoundNumberAndSurvey(roundNumber, survey)
                .orElseThrow(() -> new CustomNotFoundException(
                        String.format(LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), roundNumber),
                        LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode()));
    }

    private void checkUniquenessBySurveyAndRoundNumber(Integer roundNumber, SdssSurvey survey) {
        if (repository.existsByRoundNumberAndSurvey(roundNumber, survey)) {
            throw new CustomDataException(
                    String.format(
                            LocalizedErrorCodes.UNIQUE_SURVEY_AND_ROUND_NUMBER.getLocalizedMessage(
                                    DpCommonUtil.getUserCurrentLocale()), roundNumber + COMMA_SPACE + survey.getId()
                    ),
                    LocalizedErrorCodes.UNIQUE_SURVEY_AND_ROUND_NUMBER.getErrorCode());
        }
    }

    private void checkIfTheCurrentSurveyRoundNotActive(SdssSurveyRound surveyRound) {
        if (!surveyRound.getStatus().equals(SdssStatus.PENDING)) {
            throw new CustomValidationException(
                    String.format(LocalizedErrorCodes.UPDATES_NOT_AVAILABLE_FOR_THIS_SURVEY_ROUND.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), surveyRound.getId()),
                    LocalizedErrorCodes.UPDATES_NOT_AVAILABLE_FOR_THIS_SURVEY_ROUND.getErrorCode());
        }
    }

    private void updateTitleForSingleCreation(SdssSurveyRoundRequest request) {
        String titleEn = request.getTitleEn();
        String titleAr = request.getTitleAr();
        if (titleEn.equals(ROUND_DEFAULT_TITLE_EN)) {
            request.setTitleEn(ROUND_DEFAULT_TITLE_EN + SPACE + request.getRoundNumber());
        }
        if (titleAr.equals(ROUND_DEFAULT_TITLE_AR)) {
            request.setTitleEn(ROUND_DEFAULT_TITLE_AR + SPACE + request.getRoundNumber());
        }
    }

    private void checkIfTheCurrentRoundNeedToActiveAndThereAnotherIsActive(SdssSurvey sdssSurvey) {
        Optional<SdssSurveyRound> surveyRoundOptional = repository.findBySurveyAndActiveTrue(sdssSurvey);
        if (surveyRoundOptional.isPresent()) {
            throw new CustomDataException(
                    LocalizedErrorCodes.CANNOT_UPDATE_THE_SURVEY_ROUND_STATUS_TO_ACTIVE.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                    LocalizedErrorCodes.CANNOT_UPDATE_THE_SURVEY_ROUND_STATUS_TO_ACTIVE.getErrorCode()
            );
        }
    }

    private void checkIfSurveysMoreThanFiveRoundsForRound(Integer numberOfRounds) {
        if (numberOfRounds > 5) {
            throw new CustomDataException(
                    String.format(
                            LocalizedErrorCodes.SURVEY_ROUND_NUMBER_MUST_NOT_MORE_THAN_FIVE.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.SURVEY_ROUND_NUMBER_MUST_NOT_MORE_THAN_FIVE.getErrorCode()
            );
        }
    }
}
