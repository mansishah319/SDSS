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
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.common.config.security.jwt.JwtTokenProvider;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.core.service.EmployeeService;
import com.mgdsw.sdss.dto.surveys.SdssSurveyCreationRequest;
import com.mgdsw.sdss.dto.surveys.SdssSurveyResponse;
import com.mgdsw.sdss.dto.surveys.SurveyInvitationEmailSendResultResponse;
import com.mgdsw.sdss.dto.surveys.SurveyInvitationEmailSendSummaryResponse;
import com.mgdsw.sdss.dto.surveys.UpdateSdssSurveyStatus;
import com.mgdsw.sdss.dto.surveys.enduser.PublicEndUserSurveyResponse;
import com.mgdsw.sdss.dto.surveys.round.SdssSurveyRoundRequest;
import com.mgdsw.sdss.dto.surveys.user.SurveyUserActiveResponse;
import com.mgdsw.sdss.dto.surveys.user.SurveyUserDashboardResponse;
import com.mgdsw.sdss.dto.surveys.user.SurveyInvitedUserEmailStatusResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyInvitation;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.SdssStatus;
import com.mgdsw.sdss.enums.SurveyStatus;
import com.mgdsw.sdss.mapper.SdssSurveyMapper;
import com.mgdsw.sdss.repository.survey.ValidatorSurveyRepository;
import com.mgdsw.sdss.repository.surveys.SdssSurveyInvitationRepository;
import com.mgdsw.sdss.repository.surveys.SdssSurveyRepository;
import com.mgdsw.sdss.repository.surveys.SdssSurveyUserQuestionAnswersRepository;
import com.mgdsw.sdss.service.SdssSurveyInvitationService;
import com.mgdsw.sdss.service.SdssSurveyRoundService;
import com.mgdsw.sdss.service.SdssSurveyService;
import com.mgdsw.sdss.service.SdssSurveyUsersService;
import com.mgdsw.sdss.service.UserService;
import com.mgdsw.sdss.specification.SdssSurveySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.COLON;
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
public class SdssSurveyServiceImpl extends AbstractBaseServiceImpl<Integer, SdssSurvey> implements SdssSurveyService {

    private final SdssSurveyRepository sdssSurveyRepository;

    private final SdssSurveyInvitationService surveyInvitationService;
    private final SdssSurveyRoundService sdssSurveyRoundService;
    private final EmployeeService employeeService;
    private final UserService userService;
    private final SdssSurveyUsersService sdssSurveyUsersService;
    private final SdssSurveyMapper mapper;
    private final SdssSurveyInvitationRepository sdssSurveyInvitationRepository;
    private final SdssSurveyUserQuestionAnswersRepository sdssSurveyUserQuestionAnswersRepository;
    private final ValidatorSurveyRepository validatorSurveyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected SdssSurveyRepository repository() {
        return sdssSurveyRepository;
    }

    @Override
    @Transactional
    public SdssSurveyResponse create(SdssSurveyCreationRequest request) {
        checkIfSurveysMoreThanFiveRounds(request.getNumberOfRounds());
        User user = userService.retrieveCurrentUser();
        SdssSurvey sdssSurvey = modelMapper.map(request, SdssSurvey.class);
        if (ObjectUtils.isEmpty(request.getAllowedGrpList())) {
            sdssSurvey.setAllowedGrpList(userService.retrieveAllSdssUsersGrpNumbers());
        } else {
            sdssSurvey.setAllowedGrpList(request.getAllowedGrpList());
        }
        sdssSurvey.getAllowedGrpList().add(user.getGrpNumber());
        sdssSurvey.setStatus(SurveyStatus.NOT_STARTED);
        sdssSurvey.setActive(Boolean.FALSE);
        SdssSurvey savedSurvey = sdssSurveyRepository.save(sdssSurvey);
        List<SdssSurveyRound> sdssSurveyRoundList = this.handleRoundRelations(request, savedSurvey);
        // If the process is automatic save info and make a scheduler process, else save only.
        sdssSurveyRoundList.forEach(round -> surveyInvitationService.initInvitation(SURVEY_INVITATION_KEY, savedSurvey, round));
        return super.modelMapper.map(sdssSurvey, SdssSurveyResponse.class);
    }

    @Override
    @Transactional
    public SdssSurveyResponse update(SdssSurveyCreationRequest request, Integer surveyId) {
        checkIfSurveysMoreThanFiveRounds(request.getNumberOfRounds());
        SdssSurvey existSdssSurvey = super.findById(surveyId);
        checkSurveyNumberOfRoundsTheSameWithExistOrNot(request.getNumberOfRounds(), existSdssSurvey.getNumberOfRounds());
        checkAbilityToUpdateTheSurvey(existSdssSurvey);
        modelMapper.map(request, existSdssSurvey);
        if (Objects.nonNull(request.getDescriptionAr()))
            existSdssSurvey.setDescriptionAr(request.getDescriptionAr());
        if (Objects.nonNull(request.getDescriptionEn()))
            existSdssSurvey.setDescriptionEn(request.getDescriptionEn());
        if (ObjectUtils.isNotEmpty(request.getAllowedGrpList())) {
            existSdssSurvey.setAllowedGrpList(request.getAllowedGrpList());
        } else if (Objects.isNull(existSdssSurvey.getAllowedGrpList()) || existSdssSurvey.getAllowedGrpList().isEmpty()) {
            existSdssSurvey.setAllowedGrpList(userService.retrieveAllSdssUsersGrpNumbers());
            sdssSurveyRoundService.deleteAllBySurvey(existSdssSurvey);
            List<SdssSurveyRound> sdssSurveyRoundList = this.handleRoundRelations(request, existSdssSurvey);
            // If the process is automatic, save info and make the scheduler process else save only.
            sdssSurveyRoundList.forEach(round -> surveyInvitationService.initInvitation(SURVEY_INVITATION_KEY, existSdssSurvey, round));
        }
        existSdssSurvey.getRounds().forEach(round -> surveyInvitationService.initInvitation(SURVEY_INVITATION_KEY, existSdssSurvey, round));
        return super.modelMapper.map(sdssSurveyRepository.save(existSdssSurvey), SdssSurveyResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public SdssSurveyResponse findSurveyDetailsById(Integer surveyId) {
        SdssSurvey survey = super.findById(surveyId);
        SdssSurveyResponse response = super.modelMapper.map(survey, SdssSurveyResponse.class);
        response.setInvitedUsers(buildInvitedUserEmailStatus(survey));
        return response;
    }

    @Override
    @Transactional
    public SurveyInvitationEmailSendResultResponse resendInvitationEmail(Integer surveyId, Long employeeGrpNumber) {
        SdssSurvey survey = super.findById(surveyId);
        checkIsTheCurrentUserAllowedToDoThat(survey);
        if (Objects.isNull(survey.getAllowedGrpList()) || !survey.getAllowedGrpList().contains(employeeGrpNumber)) {
            throw new CustomAuthorizationException(
                    LocalizedErrorCodes.NOT_AUTHORIZED_TO_APPLY_THIS_ACTION.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                    LocalizedErrorCodes.NOT_AUTHORIZED_TO_APPLY_THIS_ACTION.getErrorCode()
            );
        }
        return surveyInvitationService.sendManualInvitationForUser(survey, resolveRoundForInvitation(survey), employeeGrpNumber);
    }

    @Override
    @Transactional
    public SurveyInvitationEmailSendSummaryResponse resendMissingInvitationEmails(Integer surveyId) {
        SdssSurvey survey = super.findById(surveyId);
        checkIsTheCurrentUserAllowedToDoThat(survey);
        SdssSurveyRound round = resolveRoundForInvitation(survey);
        List<SurveyInvitedUserEmailStatusResponse> missedUsers = buildInvitedUserEmailStatus(survey).stream()
                .filter(user -> !Boolean.TRUE.equals(user.getSent()))
                .collect(Collectors.toList());
        if (missedUsers.isEmpty()) {
            return SurveyInvitationEmailSendSummaryResponse.builder()
                    .totalCount(0)
                    .successCount(0)
                    .failedCount(0)
                    .allSent(Boolean.TRUE)
                    .message(translator.getMessage("survey.invitation.email.bulk.noneMissing"))
                    .reason(translator.getMessage("survey.invitation.email.bulk.reason.noneMissing"))
                    .build();
        }

        List<SurveyInvitationEmailSendResultResponse> results = missedUsers.stream()
                .map(user -> surveyInvitationService.sendManualInvitationForUser(survey, round, user.getEmployeeGrpNumber()))
                .collect(Collectors.toList());
        long successCount = results.stream().filter(result -> Boolean.TRUE.equals(result.getSent())).count();
        int failedCount = results.size() - (int) successCount;

        return SurveyInvitationEmailSendSummaryResponse.builder()
                .totalCount(results.size())
                .successCount((int) successCount)
                .failedCount(failedCount)
                .allSent(failedCount == 0)
                .message(resolveBulkMessage(results.size(), (int) successCount, failedCount))
                .reason(resolveBulkReason((int) successCount, failedCount))
                .results(results)
                .build();
    }

    @Override
    @Transactional
    public SdssSurveyResponse updateStatus(UpdateSdssSurveyStatus updateSdssSurveyStatus) {
        return this.mainUpdateStatusMethod(updateSdssSurveyStatus, false);
    }

    @Override
    @Transactional
    public SdssSurveyResponse activeRoundStatus(Integer surveyId, Integer roundNumber) {
        return this.mainUpdateStatusMethod(
                UpdateSdssSurveyStatus.builder()
                        .manual(Boolean.TRUE)
                        .surveyId(surveyId)
                        .roundNumber(roundNumber)
                        .surveyStatus(SurveyStatus.START.getEn())
                        .roundStatus(SdssStatus.IN_PROGRESS.getEn())
                        .build(),
                true
        );
    }

    @Override
    @Transactional
    public SdssSurveyResponse completedRoundStatus(Integer surveyId, Integer roundNumber) {
        return this.mainUpdateStatusMethod(
                UpdateSdssSurveyStatus.builder()
                        .manual(Boolean.TRUE)
                        .surveyId(surveyId)
                        .roundNumber(roundNumber)
                        .surveyStatus(SurveyStatus.COMPLETED.getEn())
                        .roundStatus(SdssStatus.COMPLETED.getEn())
                        .build(),
                false
        );
    }

    @Override
    @Transactional
    public SdssSurvey updateInfoOnRoundSingleCreations(Integer surveyId) {
        SdssSurvey sdssSurvey = super.findById(surveyId);
        sdssSurvey.setNumberOfRounds(sdssSurvey.getNumberOfRounds() + 1);
        return sdssSurveyRepository.save(sdssSurvey);
    }

    @Override
    @Transactional
    public String deleteById(Integer surveyId) {
        sdssSurveyRoundService.deleteAllBySurvey(super.findById(surveyId));
        return super.delete(surveyId);
    }

    @Override
    public PaginationResponse<SdssSurveyResponse> filterSurveyInfo(SdssSurveySpecification sdssSurveySpecification, Pageable pageable) {
        Page<SdssSurvey> surveyPage = super.filterReturnPage(sdssSurveySpecification, pageable);
        return super.retrievePagination(surveyPage.getContent().stream().map(mapper::toResponse).collect(Collectors.toList()), pageable, surveyPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicEndUserSurveyResponse retrieveMyCurrentActiveSurveyRound(Integer surveyId, String userInfoKey) {
        SdssSurvey sdssSurvey = super.findById(surveyId);
        return this.retrievePublicEndUserResponse(sdssSurvey, userInfoKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicEndUserSurveyResponse> retrieveMyCurrentLiveSurveys(String userInfoKey) {
        return surveyInvitationService.retrieveMyCurrentLiveSurveys(userInfoKey).stream().map(
                survey -> this.retrievePublicEndUserResponse(survey, userInfoKey)
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PublicEndUserSurveyResponse roundPreview(SdssSurveyRound sdssSurveyRound) {
        SdssSurvey survey = sdssSurveyRound.getSurvey();
        log.info("User ask for preview this survey ID : {}", survey.getId());
        checkIsTheCurrentUserAllowedToDoThat(survey);
        return mapper.toEndUerPublicResponse(survey, sdssSurveyRound);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicEndUserSurveyResponse> retrieveAllUserSurveyHistory(String userInfoKey) {
        return surveyInvitationService.retrieveMyCurrentHistorySurveys(userInfoKey).stream().map(
                round -> this.handleUserHistoryInfo(round, userInfoKey)
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllSurveysExistOnTheSystem() {
        return sdssSurveyRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyUserDashboardResponse retrieveUserDashboard(String userInfoKey) {
        if (StringUtils.isNotBlank(userInfoKey)) {
            return buildDashboardForSurveyUser(sdssSurveyUsersService.retrieveByUserInfoKey(userInfoKey), Boolean.FALSE, null);
        }
        User currentUser = userService.retrieveCurrentUser();
        SdssSurveyUsers surveyUser = sdssSurveyUsersService.retrieveByGrpNumberOrNull(currentUser.getGrpNumber());
        return buildDashboardForSurveyUser(surveyUser, Boolean.TRUE, currentUser);
    }

    private PublicEndUserSurveyResponse handleUserHistoryInfo(SdssSurveyRound round, String userInfoKey) {
        SdssSurvey survey = round.getSurvey();
        SdssSurveyInvitation sdssSurveyInvitation =
                surveyInvitationService.checkUserExistenceForAttendingSurvey(survey, userInfoKey);
        // retrieve the active round to return it to the user
        return mapper.toEndUerPublicResponse(survey, sdssSurveyInvitation);
    }

    private PublicEndUserSurveyResponse retrievePublicEndUserResponse(SdssSurvey sdssSurvey, String userInfoKey) {
        // Retrieve The Active Round
        SdssSurveyRound surveyRound =
                sdssSurveyRoundService.findActiveRound(sdssSurvey);
        // check if the user is invited for this survey or not
        SdssSurveyInvitation sdssSurveyInvitation =
                surveyInvitationService.checkUserExistenceForAttendingThisSurvey(surveyRound, userInfoKey);
        // retrieve the active round to return it to the user
        return mapper.toEndUerPublicResponse(sdssSurvey, sdssSurveyInvitation);
    }

    private void checkAbilityToUpdateTheSurvey(SdssSurvey existSdssSurvey) {
        checkIsTheCurrentUserAllowedToDoThat(existSdssSurvey);
        if (!existSdssSurvey.getStatus().equals(SurveyStatus.NOT_STARTED)) {
            throw new CustomDataException(
                    String.format(
                            LocalizedErrorCodes.CANNOT_UPDATE_THE_SURVEY_IN_PROGRESS.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.CANNOT_UPDATE_THE_SURVEY_IN_PROGRESS.getErrorCode()
            );
        }
    }

    private SdssSurveyResponse mainUpdateStatusMethod(UpdateSdssSurveyStatus updateSdssSurveyStatus,
                                                      boolean sendEmailInBackground) {
        SdssSurvey sdssSurvey = super.findById(updateSdssSurveyStatus.getSurveyId());
        this.checkIfCurrentSurveyAllowedToChangeStatus(sdssSurvey);
        this.checkIsTheCurrentUserAllowedToDoThat(sdssSurvey);
        SurveyStatus surveyStatus = SurveyStatus.fromString(updateSdssSurveyStatus.getSurveyStatus());
        sdssSurvey.setStatus(surveyStatus);
        sdssSurvey.setActive(surveyStatus.equals(SurveyStatus.START));
        SdssSurveyRound sdssSurveyRound = sdssSurveyRoundService.updateStatusBySdssSurvey(updateSdssSurveyStatus, sdssSurvey);
        if (sendEmailInBackground) {
            HttpServletRequest request = jwtTokenProvider.getRequest();
            String baseUrl = Objects.nonNull(request) ? DpCommonUtil.getBaseUrl(request) : null;
            eventPublisher.publishEvent(new SurveyRoundStatusChangedEvent(
                    sdssSurvey.getId(), sdssSurveyRound.getId(), baseUrl));
        } else {
            surveyInvitationService.sendManualInvitation(sdssSurvey, sdssSurveyRound);
        }
        return modelMapper.map(sdssSurvey, SdssSurveyResponse.class);
    }

    private void checkIfCurrentSurveyAllowedToChangeStatus(SdssSurvey sdssSurvey) {
        if (sdssSurvey.getStatus().equals(SurveyStatus.COMPLETED)) {
            throw new CustomDataException(
                    String.format(
                            LocalizedErrorCodes.CANNOT_UPDATE_THE_SURVEY_STATUS_COMPLETED.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.CANNOT_UPDATE_THE_SURVEY_STATUS_COMPLETED.getErrorCode()
            );
        }
    }

    private void checkIsTheCurrentUserAllowedToDoThat(SdssSurvey survey) {
        if (!DpCommonUtil.isTheCurrentUserAllowedToDoThisProcess(survey.getCreatedBy())) {
            throw new CustomAuthorizationException(
                    String.format(
                            LocalizedErrorCodes.NOT_AUTHORIZED_TO_APPLY_THIS_ACTION.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.NOT_AUTHORIZED_TO_APPLY_THIS_ACTION.getErrorCode()
            );
        }
    }

    private List<SurveyInvitedUserEmailStatusResponse> buildInvitedUserEmailStatus(SdssSurvey survey) {
        Map<Long, Boolean> invitationSentByGrpNumber = sdssSurveyInvitationRepository.findBySurvey(survey).stream()
                .filter(invitation -> Objects.nonNull(invitation.getInvitedUser()))
                .collect(Collectors.toMap(
                        invitation -> invitation.getInvitedUser().getEmployeeGrpNumber(),
                        invitation -> Boolean.TRUE.equals(invitation.getSent()),
                        Boolean::logicalOr,
                        LinkedHashMap::new
                ));

        return Optional.ofNullable(survey.getAllowedGrpList()).orElse(new ArrayList<>()).stream()
                .distinct()
                .map(grpNumber -> {
                    EmployeeInfoKeyResponse employeeInfo = employeeService.retrieveInfoForEmployeeById(grpNumber);
                    return SurveyInvitedUserEmailStatusResponse.builder()
                            .employeeGrpNumber(grpNumber)
                            .fullName(Objects.nonNull(employeeInfo) ? employeeInfo.getFullName() : null)
                            .emailAddress(Objects.nonNull(employeeInfo) ? employeeInfo.getEmailAddress() : null)
                            .departmentName(Objects.nonNull(employeeInfo) ? employeeInfo.getDepartmentName() : null)
                            .jobName(Objects.nonNull(employeeInfo) ? employeeInfo.getJobName() : null)
                            .sent(Boolean.TRUE.equals(invitationSentByGrpNumber.get(grpNumber)))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private SdssSurveyRound resolveRoundForInvitation(SdssSurvey survey) {
        return Optional.ofNullable(survey.getRounds()).orElse(new ArrayList<>()).stream()
                .filter(round -> Boolean.TRUE.equals(round.getActive()) || SdssStatus.IN_PROGRESS.equals(round.getStatus()))
                .findFirst()
                .orElseGet(() -> Optional.ofNullable(survey.getRounds()).orElse(new ArrayList<>()).stream()
                        .min(Comparator.comparing(SdssSurveyRound::getRoundNumber))
                        .orElseThrow(() -> new CustomDataException(
                                LocalizedErrorCodes.NO_ROUND_ACTIVE_FOR_THIS_SURVEY.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                                LocalizedErrorCodes.NO_ROUND_ACTIVE_FOR_THIS_SURVEY.getErrorCode()
                        )));
    }

    private String resolveBulkMessage(int totalCount, int successCount, int failedCount) {
        if (failedCount == 0) {
            return translator.getMessage("survey.invitation.email.bulk.allSent");
        }
        if (successCount == 0) {
            return translator.getMessage("survey.invitation.email.bulk.noneSent");
        }
        return translator.getMessage("survey.invitation.email.bulk.partialSent", Arrays.asList(successCount, totalCount));
    }

    private String resolveBulkReason(int successCount, int failedCount) {
        if (failedCount == 0) {
            return translator.getMessage("survey.invitation.email.bulk.reason.allSent");
        }
        if (successCount == 0) {
            return translator.getMessage("survey.invitation.email.bulk.reason.noneSent");
        }
        return translator.getMessage("survey.invitation.email.bulk.reason.partialSent");
    }

    private List<SdssSurveyRound> handleRoundRelations(SdssSurveyCreationRequest request, SdssSurvey survey) {
        Integer numberOfRounds = request.getNumberOfRounds();
        List<SdssSurveyRoundRequest> roundRequestList = request.getRoundRequestList();
        if (Boolean.TRUE.equals(request.getManualHandle()) && Objects.nonNull(numberOfRounds)) {
            roundRequestList =
                    IntStream.range(0, numberOfRounds)   // Loop from 0 to numberOfRounds – 1
                            .mapToObj(i ->
                                    SdssSurveyRoundRequest.builder()
                                            .manualHandle(Boolean.TRUE)
                                            .titleEn(ROUND_DEFAULT_TITLE_EN + SPACE + COLON + SPACE + (i + 1))
                                            .titleAr(ROUND_DEFAULT_TITLE_AR + SPACE + COLON + SPACE + (i + 1))
                                            .roundNumber(i + 1)
                                            .build()
                            ).collect(Collectors.toList());
        } else {
            this.checkAutomaticInfoExistOrNot(request.getRoundRequestList());
        }
        return sdssSurveyRoundService.createList(roundRequestList, survey);
    }

    private void checkAutomaticInfoExistOrNot(List<SdssSurveyRoundRequest> roundRequestList) {
        if (Objects.isNull(roundRequestList) || roundRequestList.isEmpty()) {
            throw new CustomDataException(
                    String.format(
                            LocalizedErrorCodes.ROUND_SURVEY_DATA_MISSED.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.ROUND_SURVEY_DATA_MISSED.getErrorCode()
            );
        }
    }

    private void checkIfSurveysMoreThanFiveRounds(Integer numberOfRounds) {
        if (numberOfRounds > 5) {
            throw new CustomDataException(
                    String.format(
                            LocalizedErrorCodes.SURVEY_ROUND_NUMBER_MUST_NOT_MORE_THAN_FIVE.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.SURVEY_ROUND_NUMBER_MUST_NOT_MORE_THAN_FIVE.getErrorCode()
            );
        }
    }

    private void checkSurveyNumberOfRoundsTheSameWithExistOrNot(Integer requestNumberOfRounds, Integer existNumberOfRounds) {
        if (Objects.nonNull(requestNumberOfRounds) && !requestNumberOfRounds.equals(existNumberOfRounds)) {
            throw new CustomDataException(
                    String.format(
                            LocalizedErrorCodes.NUMBER_OF_ROUNDS_NOT_APPLICABLE_TO_UPDATE.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.NUMBER_OF_ROUNDS_NOT_APPLICABLE_TO_UPDATE.getErrorCode()
            );
        }
    }

    private SurveyUserDashboardResponse buildDashboardForSurveyUser(SdssSurveyUsers surveyUser, Boolean internalUser, User currentUser) {
        List<SdssSurvey> surveys = new ArrayList<>();
        Set<Integer> answeredSurveyIds = new HashSet<>();
        long totalAnswersCount = 0L;
        LocalDateTime lastActivityDate = null;
        EmployeeInfoKeyResponse employeeInfo = Objects.nonNull(surveyUser) && Objects.isNull(currentUser) ?
                employeeService.retrieveInfoForEmployeeById(surveyUser.getEmployeeGrpNumber()) : null;

        if (Objects.nonNull(surveyUser)) {
            surveys = sdssSurveyInvitationRepository.findByInvitedUser(surveyUser).stream()
                    .map(SdssSurveyInvitation::getSurvey)
                    .filter(Objects::nonNull)
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(SdssSurvey::getId, survey -> survey, (first, second) -> first, LinkedHashMap::new),
                            map -> new ArrayList<>(map.values())
                    ));
            answeredSurveyIds = new HashSet<>(sdssSurveyUserQuestionAnswersRepository.findDistinctAnsweredSurveyIdsByUser(surveyUser));
            totalAnswersCount = sdssSurveyUserQuestionAnswersRepository.countByUser(surveyUser);
            lastActivityDate = sdssSurveyUserQuestionAnswersRepository.findLastActivityDateByUser(surveyUser);
        }

        Set<Integer> finalAnsweredSurveyIds = answeredSurveyIds;
        List<SurveyUserActiveResponse> mappedSurveys = surveys.stream()
                .map(survey -> mapSurveyDashboardItem(survey, finalAnsweredSurveyIds.contains(survey.getId())))
                .collect(Collectors.toList());

        List<SurveyUserActiveResponse> activeSurveys = mappedSurveys.stream()
                .filter(survey -> SurveyStatus.START.name().equals(survey.getStatus()))
                .collect(Collectors.toList());
        List<SurveyUserActiveResponse> completedSurveys = mappedSurveys.stream()
                .filter(survey -> SurveyStatus.COMPLETED.name().equals(survey.getStatus()))
                .collect(Collectors.toList());
        List<SurveyUserActiveResponse> pendingSurveys = mappedSurveys.stream()
                .filter(survey -> !survey.isHasAnswered() && !SurveyStatus.COMPLETED.name().equals(survey.getStatus()))
                .collect(Collectors.toList());

        long totalSurveys = surveys.size();
        long answeredSurveys = mappedSurveys.stream().filter(SurveyUserActiveResponse::isHasAnswered).count();
        long pendingSurveyCount = pendingSurveys.size();
        Boolean delphiModerator = Boolean.FALSE;
        Long employeeGrpNumber = null;
        String employeeFullName = null;
        String departmentName = null;
        String jobTitle = null;

        if (Objects.nonNull(currentUser)) {
            delphiModerator = Boolean.TRUE.equals(currentUser.getIsDelphiModerator());
            employeeGrpNumber = currentUser.getGrpNumber();
            employeeFullName = currentUser.getFullName();
            departmentName = currentUser.getDepartmentName();
            jobTitle = currentUser.getJobName();
        } else if (Objects.nonNull(surveyUser)) {
            employeeGrpNumber = surveyUser.getEmployeeGrpNumber();
            if (Objects.nonNull(employeeInfo)) {
                employeeFullName = employeeInfo.getFullName();
                departmentName = employeeInfo.getDepartmentName();
                jobTitle = employeeInfo.getJobName();
            }
        }

        SurveyUserDashboardResponse.SurveyUserDashboardResponseBuilder builder = SurveyUserDashboardResponse.builder()
                .internalUser(internalUser)
                .delphiModerator(delphiModerator)
                .employeeGrpNumber(employeeGrpNumber)
                .employeeFullName(employeeFullName)
                .departmentName(departmentName)
                .jobTitle(jobTitle)
                .totalSurveys(totalSurveys)
                .answeredSurveys(answeredSurveys)
                .pendingSurveys(pendingSurveyCount)
                .participationRate(totalSurveys == 0 ? 0D : (answeredSurveys * 100.0D) / totalSurveys)
                .recentActivity(totalAnswersCount)
                .lastActivityDate(lastActivityDate)
                .activeSurveysCount(activeSurveys.size())
                .pendingSurveysCount(pendingSurveys.size())
                .completedSurveysCount(completedSurveys.size())
                .totalAnswersCount(totalAnswersCount)
                .activeSurveysList(activeSurveys)
                .pendingSurveysList(pendingSurveys)
                .completedSurveysList(completedSurveys);

        if (Objects.nonNull(currentUser) && Boolean.TRUE.equals(currentUser.getIsDelphiModerator())) {
            builder.totalReviewsMade(validatorSurveyRepository.countAllByUser(currentUser))
                    .reviewedSolutionsCount(validatorSurveyRepository.countDistinctSolutionsByUser(currentUser))
                    .reviewedChallengesCount(validatorSurveyRepository.countDistinctChallengesByUser(currentUser));
        }

        return builder.build();
    }

    private SurveyUserActiveResponse mapSurveyDashboardItem(SdssSurvey survey, boolean hasAnswered) {
        return SurveyUserActiveResponse.builder()
                .surveyId(survey.getId())
                .surveyNameEn(survey.getTitleEn())
                .surveyNameAr(survey.getTitleAr())
                .descriptionEn(survey.getDescriptionEn())
                .descriptionAr(survey.getDescriptionAr())
                .totalRounds(survey.getNumberOfRounds())
                .activeRounds(Objects.nonNull(survey.getRounds()) ?
                        survey.getRounds().stream()
                        .filter(round -> Boolean.TRUE.equals(round.getActive()) || SdssStatus.IN_PROGRESS.equals(round.getStatus()))
                        .count() : 0L)
                .hasAnswered(hasAnswered)
                .status(Objects.nonNull(survey.getStatus()) ? survey.getStatus().name() : null)
                .build();
    }
}
