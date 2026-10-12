/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.challenge;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.config.security.app.AppConfiguration;
import com.mgdsw.sdss.common.config.security.jwt.JwtTokenProvider;
import com.mgdsw.sdss.common.enums.EmailSubject;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.enums.LocalizedSuccessCodes;
import com.mgdsw.sdss.common.helper.pdf.PdfGenerator;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.common.utils.DpDateUtil;
import com.mgdsw.sdss.core.entity.Employee;
import com.mgdsw.sdss.core.service.EmployeeService;
import com.mgdsw.sdss.dto.challenge.ChallengeAdminDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeAttendeesResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeDocsResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeForUserDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeSearchRequest;
import com.mgdsw.sdss.dto.challenge.ChallengesCreationRequest;
import com.mgdsw.sdss.dto.challenge.ChallengesResponse;
import com.mgdsw.sdss.dto.challenge.DashboardChallengesStrategicResponse;
import com.mgdsw.sdss.dto.challenge.SimpleChallengesResponse;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsResponse;
import com.mgdsw.sdss.dto.solution.SolutionFullResponse;
import com.mgdsw.sdss.dto.solution.SolutionResponse;
import com.mgdsw.sdss.dto.strategic.dashboard.DashboardFutureStrategicCommonResponse;
import com.mgdsw.sdss.dto.user.ChallengeUsersResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import com.mgdsw.sdss.email.service.EmailSender;
import com.mgdsw.sdss.entity.challenge.ChallengeVideo;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.entity.strategic.FutureStrategicDirection;
import com.mgdsw.sdss.entity.strategic.FutureStrategicGoals;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.enums.ChallengeStatus;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.Operations;
import com.mgdsw.sdss.enums.SurveyStatus;
import com.mgdsw.sdss.enums.SystemRoles;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import com.mgdsw.sdss.helper.TemplateHelper;
import com.mgdsw.sdss.helper.template.TemplateDto;
import com.mgdsw.sdss.mapper.ChallengesMapper;
import com.mgdsw.sdss.repository.challenge.ChallengesRepository;
import com.mgdsw.sdss.service.ChallengeDocumentsService;
import com.mgdsw.sdss.service.ChallengeHashtagService;
import com.mgdsw.sdss.service.ChallengeReminderService;
import com.mgdsw.sdss.service.ChallengeSettingsService;
import com.mgdsw.sdss.service.ChallengeVideoService;
import com.mgdsw.sdss.service.ChallengesService;
import com.mgdsw.sdss.service.ChatMessagesService;
import com.mgdsw.sdss.service.EmailTemplatesService;
import com.mgdsw.sdss.service.FutureStrategicDirectionService;
import com.mgdsw.sdss.service.FutureStrategicGoalsService;
import com.mgdsw.sdss.service.HashtagService;
import com.mgdsw.sdss.service.SolutionService;
import com.mgdsw.sdss.service.StrategicDirectionService;
import com.mgdsw.sdss.service.StrategicGoalsService;
import com.mgdsw.sdss.service.UserInvitationService;
import com.mgdsw.sdss.service.UserService;
import com.mgdsw.sdss.service.ValidatorsService;
import com.mgdsw.sdss.service.VotingService;
import com.mgdsw.sdss.specification.ChallengeSpecification;
import com.mgdsw.sdss.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.ROLE_PREFIX;
import static com.mgdsw.sdss.common.enums.EmailSubject.VALIDATOR_NEW_VALIDATE_REMINDER_SUBJECT;
import static com.mgdsw.sdss.common.enums.EmailSubject.WELCOME_SUBJECT;
import static com.mgdsw.sdss.common.helper.pdf.constants.HtmlTemplateConstants.CHALLENGE_DETAILS_REPORT_TEMPLATE;
import static com.mgdsw.sdss.common.utils.TranslatorUtil.ClassFiledNames.Users.CHALLENGE_SETTINGS_TITLE;
import static com.mgdsw.sdss.common.utils.TranslatorUtil.Message.NOT_FOUND;
import static com.mgdsw.sdss.enums.TemplatesTypeKeys.VALIDATOR_NEW_VALIDATE_REMINDER_KEY;
import static com.mgdsw.sdss.enums.TemplatesTypeKeys.WELCOME_KEY;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class ChallengesServiceImpl extends AbstractBaseServiceImpl<Integer, Challenges> implements ChallengesService {

    private final ChallengesRepository challengesRepository;

    private final FutureStrategicDirectionService futureStrategicDirectionService;
    private final FutureStrategicGoalsService futureStrategicGoalsService;
    private final StrategicDirectionService strategicDirectionService;
    private final StrategicGoalsService strategicGoalsService;

    private final ChallengeDocumentsService challengeDocumentsService;
    private final ChallengeReminderService challengeReminderService;
    private final ChallengeSettingsService challengeSettingsService;
    private final ChallengeHashtagService challengeHashtagService;
    private final EmailTemplatesService emailTemplatesService;
    private final ChallengeVideoService challengeVideoService;
    private final UserInvitationService userInvitationService;
    private final ChatMessagesService chatMessagesService;
    private final ValidatorsService validatorsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmployeeService employeeService;
    private final SolutionService solutionService;
    private final HashtagService hashtagService;
    private final VotingService votingService;
    private final UserService userService;
    private final EmailSender emailSender;
    private final PdfGenerator pdfGenerator;
    private final ApplicationEventPublisher eventPublisher;

    private final ChallengesMapper challengesMapper;
    private final AppConfiguration appConfiguration;

    @Override
    protected ChallengesRepository repository() {
        return challengesRepository;
    }

    @Override
    @Transactional
    public ChallengesResponse create(ChallengesCreationRequest request, List<MultipartFile> files) {
        // Check Challenge Settings Ids
        this.checkChallengeRequestSettingsIds(request);
        // Map Challenges
        Challenges challenges = super.modelMapper.map(request, Challenges.class);
        // Add moderator
        this.handleModerator(request, challenges);
        // Set mandatory data
        this.handleMandatoryData(request, challenges);
        // Set Strategic values
        this.prepareStrategicToSave(request, challenges);
        // Save Challenge
        Challenges savedChallenge = challengesRepository.save(challenges);
        // Save documents from the same request before preparing the rest of the relations.
        if (Objects.nonNull(files) && !files.isEmpty()) {
            challengeDocumentsService.createList(files, savedChallenge.getId());
        }
        // Save invited user to the challenge (userInvitationService)
        this.handleChallengesRelations(request, savedChallenge, Operations.CREATE);
        eventPublisher.publishEvent(new ChallengeInvitationEmailEvent(savedChallenge.getId(), request.getChallengeRemindersDateTimeList()));
        // Return the response
        return modelMapper.map(savedChallenge, ChallengesResponse.class);
    }

    @Override
    @Transactional
    public ChallengesResponse update(Integer challengeId, ChallengesCreationRequest request, List<MultipartFile> files) {
        // Check Challenge Settings Ids
        this.checkChallengeRequestSettingsIds(request);

        Challenges updatedChallenges = this.mapUpdatedChallengeData(this.findById(challengeId), request);

        this.prepareStrategicToSave(request, updatedChallenges);

        // Add moderator
        if (Objects.nonNull(request.getModeratorId())) {
            this.handleModerator(request, updatedChallenges);
        }
        Challenges updatedChallenge = challengesRepository.save(updatedChallenges);
        if (Objects.nonNull(files) && !files.isEmpty()) {
            challengeDocumentsService.createList(files, updatedChallenge.getId());
        }
        // Save invited user to the challenge (userInvitationService)
        this.handleChallengesRelations(request, updatedChallenge, Operations.UPDATE);
        eventPublisher.publishEvent(new ChallengeInvitationEmailEvent(updatedChallenge.getId(), request.getChallengeRemindersDateTimeList()));
        return modelMapper.map(updatedChallenge, ChallengesResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ChallengesResponse> filter(ChallengeSearchRequest searchRequest, Pageable pageable) {
        return this.handleChellangePaginationResponse(super.filter(new ChallengeSpecification(searchRequest), pageable), searchRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengesResponse viewChallengeScreen(Integer challengeId) {
        return this.prepareChallengeViewResponse(super.findById(challengeId));
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengeAttendeesResponse retrieveAllUsersInvitedToThisChallengeId(Integer challengeId) {
        Challenges challenge = this.findById(challengeId);
        return retrieveAttendeesUsers(challenge);
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengeDashboardResponse retrieveChallengeDashboardResponse(Integer challengeId) {
        boolean isUserLanguageEnglish = userService.retrieveUserLanguage().equals(Language.EN);
        Challenges challenges = this.findById(challengeId);
        return ChallengeDashboardResponse.builder()
                .challengeDetails(isUserLanguageEnglish ? challenges.getDescriptionEn() : challenges.getDescriptionAr())
                .challengeTitle(isUserLanguageEnglish ? challenges.getTitleEn() : challenges.getTitleAr())
                .startTime(DpDateUtil.toStringDateTime(challenges.getStartTime()))
                .endTime(DpDateUtil.toStringDateTime(challenges.getEndTime()))
                .youtubeUrls(challenges.getYoutube().stream().map(ChallengeVideo::getLink).collect(Collectors.toList()))
                .challengeSolutions(this.prepareChallengeDashboardSolutionsBasedOnUserRole(challenges))
                .moderator(Objects.nonNull(challenges.getModerator()) ?
                        userService.findById(challenges.getModerator().getId(), UserDropDownListResponse.class) : null)
                .assistantModerator(Objects.nonNull(challenges.assistantModerator) ?
                        userService.findById(challenges.getAssistantModerator().getId(), UserDropDownListResponse.class) : null)
                .queLevel(challenges.getQueLevel())
                .status(challenges.getStatus())
                .isFinal(challenges.getIsFinal())
                .build();
    }

    @Override
    @Transactional
    public void updateChallengeRoundLevel(Challenges challenges, Integer roundLevel, Integer isFinal) {
        int queueLevel = Objects.nonNull(roundLevel) ? roundLevel : challenges.getQueLevel() + 1;
        challenges.setQueLevel(queueLevel);
        challenges.getStageList().add(queueLevel);
        if (Objects.nonNull(isFinal) && isFinal == 1) {
            challenges.setIsFinal(isFinal);
            challenges.setIsSendSurvey(Boolean.TRUE);
            challenges.setSurveyStatus(SurveyStatus.START);
            challenges.setStatus(ChallengeStatus.UNDER_VALIDATOR_REVIEW);
        } else {
            log.info("The updated Challenge Round is --> {}", challenges.getQueLevel());
            challenges.setStatus(ChallengeStatus.VOTING);
        }
        challengesRepository.save(challenges);
    }

    @Override
    public ChallengesResponse updateChallengeSurveyStatus(Integer challengeId, SurveyStatus surveyStatus) {
        Challenges challenges = this.findById(challengeId);
        challenges.setSurveyStatus(surveyStatus);
        return super.modelMapper.map(challengesRepository.save(challenges), ChallengesResponse.class);
    }

    @Override
    public SimpleChallengesResponse updateChallengeStatus(Integer challengeId, ChallengeStatus status) {
        Challenges challenges = this.findById(challengeId);
        challenges.setStatus(status);
        if (status.equals(ChallengeStatus.COMPLETED) || status.equals(ChallengeStatus.CLOSED)) {
            this.validateUserAuthorityBasedOnStatus(status);
            challenges.setIsFinal(1);
        } else challenges.setIsFinal(0);
        Challenges savedChallengeAfterUpdatingStatus = challengesRepository.save(challenges);
        return super.modelMapper.map(savedChallengeAfterUpdatingStatus, SimpleChallengesResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeAdminDashboardResponse> retrieveAllChallengesBasedOnStatus(ChallengeStatus status) {
        return super.mapList(challengesRepository.findAllByStatus(status), ChallengeAdminDashboardResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeAdminDashboardResponse> retrieveAllChallengesBasedOnStatusForCurrentUser(ChallengeStatus status) {
        return super.mapList(
                userInvitationService.findChallengesByUser(userService.retrieveCurrentUser()).stream()
                        .filter(challenge -> status.equals(challenge.getStatus()))
                        .collect(Collectors.toList()),
                ChallengeAdminDashboardResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeAdminDashboardResponse> retrieveAllChallenges() {
        return super.mapList(challengesRepository.findAll(), ChallengeAdminDashboardResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeAdminDashboardResponse> retrieveAllChallengesForCurrentUser() {
        return super.mapList(userInvitationService.findChallengesByUser(userService.retrieveCurrentUser()), ChallengeAdminDashboardResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllChallengesBasedOnStatus(ChallengeStatus status) {
        return challengesRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllChallengesBasedOnStatusForCurrentUser(ChallengeStatus status) {
        return userInvitationService.findChallengesByUser(userService.retrieveCurrentUser()).stream()
                .filter(challenge -> status.equals(challenge.getStatus()))
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllChallenges() {
        return challengesRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllChallengesForCurrentUser() {
        return userInvitationService.findChallengesByUser(userService.retrieveCurrentUser()).size();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ChallengesResponse> filterBasedOnInvitedUsers(ChallengeSearchRequest searchRequest, Pageable pageable) {
        searchRequest.setInvitedUserId(DpCommonUtil.getUserPayload().getGrpNumber());
        Set<String> authorities = DpCommonUtil.retrieveCurrentUserAuthorities();

        PaginationResponse<ChallengesResponse> challengesResponsePaginationResponse =
                this.handleChellangePaginationResponse(
                        super.filter(new ChallengeSpecification(searchRequest), pageable),
                        searchRequest
                );

        // If Commander, Admin, or Moderator → return full response
        if (authorities.contains(SystemRoles.COMMANDER.getKey()) ||
                authorities.contains(SystemRoles.ADMIN.getKey()) ||
                authorities.contains(SystemRoles.MODERATOR.getKey())) {
            return challengesResponsePaginationResponse;
        }

        // Otherwise → strip down the response
        List<ChallengesResponse> sanitizedList = challengesResponsePaginationResponse
                .getData()
                .stream()
                .map(res -> ChallengesResponse.builder()
                        .id(res.getId()) // from BaseResponse<Integer>
                        .localTitle(res.getLocalTitle())
                        .localDescription(res.getLocalDescription())
                        .moderator(res.getModerator())
                        .departmentId(res.getDepartmentId())
                        .departmentName(res.getDepartmentName())
                        .startTime(res.getStartTime())
                        .endTime(res.getEndTime())
                        .visibility(res.getVisibility())
                        .createdAt(res.getCreatedAt())
                        .updatedAt(res.getUpdatedAt())
                        .status(res.getStatus())
                        .allowImmediateAccess(res.isAllowImmediateAccess())
                        .build()
                )
                .collect(Collectors.toList());

        // Replace the data list with the sanitized one
        challengesResponsePaginationResponse.setData(sanitizedList);

        return challengesResponsePaginationResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeForUserDashboardResponse> prepareTheChallengesForTheCurrentUserDashboardBasedOnStatusResponse(
            List<Challenges> challengesList, ChallengeStatus status, SystemRoles neededRole) {

        // Function to map a Challenge to its Response object with basic properties
        Function<Challenges, ChallengeForUserDashboardResponse> mapToResponse = challenge -> {
            ChallengeForUserDashboardResponse response = modelMapper.map(challenge, ChallengeForUserDashboardResponse.class);
            if (neededRole.equals(SystemRoles.MODERATOR)) {
                response.setUserInvitations(userInvitationService.retrieveTheCurrentUserInvitation(challenge));
            }

            if (neededRole.equals(SystemRoles.VALIDATOR)) {
                response.setUserInvitations(validatorsService.retrieveTheCurrentValidatorInvitation(challenge));
            }

            // Conditionally add final decisions if the challenge is completed
            if (status == ChallengeStatus.COMPLETED) {
                response.setFinalDecisions(solutionService.findThFinalSolutionsByChallengeId(challenge.getId()));
            }
            return response;
        };

        // Filter based on the provided status if non-null, otherwise return all
        Stream<Challenges> filteredChallenges = challengesList.stream();
        if (status != null) {
            filteredChallenges = filteredChallenges.filter(challenge -> challenge.getStatus().equals(status));
        }

        // Map filtered challenges to response objects
        return filteredChallenges.map(mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeAdminDashboardResponse> prepareTheChallengesForTheCurrentValidatorUserDashboardBasedOnStatusResponse(List<Challenges> challenesList,
                                                                                                                              ChallengeStatus status) {
        if (Objects.nonNull(status)) {
            if (status.equals(ChallengeStatus.COMPLETED))
                return challenesList.stream().filter(challenge -> challenge.getStatus().equals(status)).map(ch -> {
                    ChallengeAdminDashboardResponse userDashboardResponse = modelMapper.map(ch, ChallengeAdminDashboardResponse.class);
                    userDashboardResponse.setFinalDecisions(solutionService.findThFinalSolutionsByChallengeId(ch.getId()));
                    return userDashboardResponse;
                }).collect(Collectors.toList());
            else
                return challenesList.stream().filter(challenge -> challenge.getStatus().equals(status)).map(ch ->
                        modelMapper.map(ch, ChallengeAdminDashboardResponse.class)
                ).collect(Collectors.toList());
        } else {
            return challenesList.stream().map(ch ->
                    modelMapper.map(ch, ChallengeAdminDashboardResponse.class)).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public String delete(Integer challengeId) {
        Challenges challengeNeedToRemove = this.findById(challengeId);
        // userInvitations
        userInvitationService.removeAllByChallenge(challengeNeedToRemove);
        // solutions
        solutionService.removeAllByChallenge(challengeNeedToRemove);
        // youtube
        challengeVideoService.removeAllByChallenge(challengeNeedToRemove);
        // challengeReminders
        challengeReminderService.removeAllByChallenge(challengeNeedToRemove);
        // validators
        validatorsService.removeAllByChallenge(challengeNeedToRemove);
        // votingList
        votingService.removeAllByChallenge(challengeNeedToRemove);
        // now delete a challenge
        challengesRepository.delete(challengeNeedToRemove);
        return LocalizedSuccessCodes.DELETED_SUCCESSFULLY.getLocalizedMessage();
    }

    @Override
    public String welcomeStartVotingEmail(Integer challengeId) {
        try {
            Challenges challenges = this.findById(challengeId);
            List<Long> allInvitedUsersIds =
                    this.getAllInvitedUsers(
                            this.getInvitedInternalAdminUsers(challenges),
                            this.getInvitedValidatorUsers(challenges)
                    ).stream().map(ChallengeUsersResponse::getGrpNumber).collect(Collectors.toList());
            this.prepareEmailBasedOnTemplateTypeKey(
                    allInvitedUsersIds,
                    challenges,
                    WELCOME_KEY,
                    appConfiguration.getChallengeDashboardUri() + challengeId,
                    WELCOME_SUBJECT
            );
            return "Welcome Email Sent Successfully.";
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return "There is an issue happened while sending a welcome start voting email : " + exception.getMessage();
        }
    }

    @Override
    public String validatorStartValidateNewChallengeEmail(Integer challengeId) {
        try {
            Challenges challenges = this.findById(challengeId);
            List<Long> validatorIds =
                    this.getInvitedValidatorUsers(challenges)
                            .stream().map(ChallengeUsersResponse::getGrpNumber).collect(Collectors.toList());
            this.prepareEmailBasedOnTemplateTypeKey(
                    validatorIds,
                    challenges,
                    VALIDATOR_NEW_VALIDATE_REMINDER_KEY,
                    appConfiguration.getValidatorRequestsToValidate(),
                    VALIDATOR_NEW_VALIDATE_REMINDER_SUBJECT
            );
            return "New Challenge for Validator Email Sent Successfully.";
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return "There is an issue happened while sending a validator required validate the new challenge email : " + exception.getMessage();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByDepartmentId(String id) {
        return challengesRepository.countByDepartmentId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardFutureStrategicCommonResponse> retrieveDashboardFutureStrategicGoalsResponse() {
        return futureStrategicGoalsService.getAll().stream().map(this::prepareSingleDashboardFutureStrategicGoalsResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardFutureStrategicCommonResponse> retrieveDashboardFutureStrategicDirectionsResponse() {
        return futureStrategicDirectionService.getAll().stream().map(this::prepareSingleDashboardFutureStrategicDirectionResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateChallengeReportPDF(Integer challengeId) {
        ChallengesResponse challengesResponse = this.prepareChallengeViewResponse(super.findById(challengeId));
        return pdfGenerator.createPdf(CHALLENGE_DETAILS_REPORT_TEMPLATE, challengesResponse, Language.AR);
    }

    private DashboardFutureStrategicCommonResponse prepareSingleDashboardFutureStrategicGoalsResponse(FutureStrategicGoals futureStrategicGoals) {
        DashboardFutureStrategicCommonResponse dashboardFutureStrategicGoalsResponse = modelMapper.map(futureStrategicGoals, DashboardFutureStrategicCommonResponse.class);
        dashboardFutureStrategicGoalsResponse.setCountOfChallenges(this.countByFutureStrategicGoals(futureStrategicGoals));
        dashboardFutureStrategicGoalsResponse.setChallengesResponseList(this.retrieveAllChallengesByFutureStrategicGoals(futureStrategicGoals));
        return dashboardFutureStrategicGoalsResponse;
    }

    private DashboardFutureStrategicCommonResponse prepareSingleDashboardFutureStrategicDirectionResponse(FutureStrategicDirection futureStrategicDirection) {
        DashboardFutureStrategicCommonResponse dashboardFutureStrategicGoalsResponse = modelMapper.map(futureStrategicDirection, DashboardFutureStrategicCommonResponse.class);
        dashboardFutureStrategicGoalsResponse.setCountOfChallenges(this.countByFutureStrategicDirection(futureStrategicDirection));
        dashboardFutureStrategicGoalsResponse.setChallengesResponseList(this.retrieveAllChallengesByFutureStrategicDirection(futureStrategicDirection));
        return dashboardFutureStrategicGoalsResponse;
    }

    private long countByFutureStrategicGoals(FutureStrategicGoals futureStrategicGoals) {
        return challengesRepository.countByFutureStrategicGoals(futureStrategicGoals);
    }

    private long countByFutureStrategicDirection(FutureStrategicDirection futureStrategicDirection) {
        return challengesRepository.countByFutureStrategicDirection(futureStrategicDirection);
    }

    private List<DashboardChallengesStrategicResponse> retrieveAllChallengesByFutureStrategicGoals(FutureStrategicGoals futureStrategicGoals) {
        return challengesRepository.findByFutureStrategicGoals(futureStrategicGoals)
                .stream().map(challenge -> modelMapper.map(challenge, DashboardChallengesStrategicResponse.class)).collect(Collectors.toList());
    }

    private List<DashboardChallengesStrategicResponse> retrieveAllChallengesByFutureStrategicDirection(FutureStrategicDirection futureStrategicDirection) {
        return challengesRepository.findByFutureStrategicDirection(futureStrategicDirection)
                .stream().map(challenge -> modelMapper.map(challenge, DashboardChallengesStrategicResponse.class)).collect(Collectors.toList());
    }

    private void prepareEmailBasedOnTemplateTypeKey(List<Long> allInvitedUsersIds,
                                                    Challenges challenges,
                                                    TemplatesTypeKeys templatesTypeKey,
                                                    String inviteUriLink,
                                                    EmailSubject emailSubject
    ) {
        allInvitedUsersIds.forEach(userId -> {
            User user = userService.findUserByGrpNumber(userId);
            HttpServletRequest request = jwtTokenProvider.getRequest();
            EmailTemplates emailTemplates = emailTemplatesService.retrieveActiveEmailTemplatesByTemplateType(templatesTypeKey.toString());
            boolean isEmailTemplatesNonNull = Objects.nonNull(emailTemplates);
            emailSender.sendEmail(
                    TemplateHelper.prepareEmail(
                            TemplateDto.builder()
                                    .inviteLink(DpCommonUtil.getBaseUrl(request) + inviteUriLink)
                                    .subjectEn(isEmailTemplatesNonNull ? emailTemplates.getSubjectAr() : emailSubject.getAr())
                                    .subjectAr(isEmailTemplatesNonNull ? emailTemplates.getSubjectAr() : emailSubject.getEn())
                                    .contentAr(isEmailTemplatesNonNull ? emailTemplates.getContentAr() : null)
                                    .contentEn(isEmailTemplatesNonNull ? emailTemplates.getContentEn() : null)
                                    .path(TemplateHelper.determinePathByTemplateType(templatesTypeKey))
                                    .isLanguageEnglish(UserUtils.isUserLanguageEnglish(user))
                                    .emailList(UserUtils.getUserEmails(user))
                                    .challengeSessionLink(null)
                                    .challenge(challenges)
                                    .user(user)
                                    .build()
                    ));
        });
    }

    private void validateUserAuthorityBasedOnStatus(ChallengeStatus status) {
        if (isUnauthorizedUserForDecision(status)) {
            throw new CustomAuthorizationException(
                    LocalizedErrorCodes.SDSS_UNAUTHORIZED_USER_FOR_DECISION.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                    LocalizedErrorCodes.SDSS_UNAUTHORIZED_USER_FOR_DECISION.getErrorCode()
            );
        }
    }

    private boolean isUnauthorizedUserForDecision(ChallengeStatus status) {
        if (status.equals(ChallengeStatus.COMPLETED) && !userHasRole(SystemRoles.ADMIN)) {
            return true;
        } else if (status.equals(ChallengeStatus.CLOSED) && !(userHasRole(SystemRoles.ADMIN) || userHasRole(SystemRoles.MODERATOR))) {
            return true;
        } else return status.equals(ChallengeStatus.UNDER_ADMIN_REVIEW) && !userHasRole(SystemRoles.VALIDATOR);
    }

    private boolean userHasRole(SystemRoles role) {
        return DpCommonUtil.getUserPayload()
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals(role.getKey()));
    }

    private Challenges mapUpdatedChallengeData(Challenges challenges, ChallengesCreationRequest request) {
        modelMapper.map(request, challenges);
        challenges.setStartTime(DpDateUtil.toLocalDateTime(request.getStartTime()));
        challenges.setEndTime(DpDateUtil.toLocalDateTime(request.getEndTime()));
        return challenges;
    }

    private ChallengeAttendeesResponse retrieveAttendeesUsers(Challenges challenge) {
        // Retrieve all invited internal admin users
        List<ChallengeUsersResponse> invitedInternalAdminUsers = getInvitedInternalAdminUsers(challenge);

        // Retrieve all invited validator users
        List<ChallengeUsersResponse> invitedValidatorUsers = getInvitedValidatorUsers(challenge);

        // Combine admin and validator users and remove duplicates
        List<ChallengeUsersResponse> allUsers = getAllInvitedUsers(invitedInternalAdminUsers, invitedValidatorUsers);

        // Count online and offline users
        long countOnlineUsers = countUsersBasedOnOnlineStatus(allUsers, Boolean.TRUE);
        long countOfflineUsers = countUsersBasedOnOnlineStatus(allUsers, Boolean.FALSE);

        // Build and return the ChallengeAttendeesResponse
        return ChallengeAttendeesResponse.builder()
                .invitedValidatorUsers(invitedValidatorUsers)
                .invitedAdminUsers(invitedInternalAdminUsers)
                .offlineCount(countOfflineUsers)
                .onlineCount(countOnlineUsers)
                .allAttendeesUsers(allUsers)
                .build();
    }

    private List<ChallengeUsersResponse> getInvitedInternalAdminUsers(Challenges challenge) {
        return userInvitationService.findAllInvitedUsersByChallenges(challenge)
                .stream().map(invitedUser -> {
                    Employee employee = this.findOptionalEmployee(invitedUser.getGrpNumber());
                    log.info("The Team member Invited Grp Number is → {}", invitedUser.getGrpNumber());
                    invitedUser.setFullName(Objects.nonNull(employee) ? employee.getFullName() : "default");
                    return invitedUser;
                }).collect(Collectors.toList());
    }

    private List<ChallengeUsersResponse> getInvitedValidatorUsers(Challenges challenge) {
        return validatorsService.findAllInvitedValidatorUsersByChallenges(challenge)
                .stream().map(invitedValidator -> {
                    ChallengeUsersResponse challengeUsersResponse = super.modelMapper.map(invitedValidator, ChallengeUsersResponse.class);
                    Employee employee = this.findOptionalEmployee(invitedValidator.getGrpNumber());
                    log.info("The Validator User Grp Number is → {}", invitedValidator.getGrpNumber());
                    challengeUsersResponse.setFullName(Objects.nonNull(employee) ? employee.getFullName() : "default");
                    return challengeUsersResponse;
                }).collect(Collectors.toList());
    }

    private List<ChallengeUsersResponse> getAllInvitedUsers(List<ChallengeUsersResponse> invitedInternalAdminUsers, List<ChallengeUsersResponse> invitedValidatorUsers) {
        return new ArrayList<>(Stream.concat(invitedInternalAdminUsers.stream(), invitedValidatorUsers.stream())
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.toMap(ChallengeUsersResponse::getId, obj -> obj, (existing, replacement) -> existing))
                .values());
    }

    private Employee findOptionalEmployee(Long grpNumber) {
        return employeeService.findEmployeeByIdOrNull(grpNumber);
    }

    private long countUsersBasedOnOnlineStatus(List<ChallengeUsersResponse> invitedUsers, Boolean onlineStatus) {
        return invitedUsers.stream()
                .filter(user -> onlineStatus.equals(user.getOnlineStatus()))
                .count();
    }

    private PaginationResponse<ChallengesResponse> handleChellangePaginationResponse(PaginationResponse<Challenges> result,
                                                                                     ChallengeSearchRequest searchRequest
    ) {
        List<ChallengesResponse> userResponseList =
                result.getData().stream().map(this::mapChallengeDataBasedOnUserLanguage).collect(Collectors.toList());
        if (!userResponseList.isEmpty() && Objects.nonNull(searchRequest.getDepartment())) {
            List<ChallengesResponse> userResponses = userResponseList.stream().filter(userResponse ->
                    Objects.nonNull(userResponse.getDepartmentName())
                            && userResponse.getDepartmentName().contains(searchRequest.getDepartment())
            ).collect(Collectors.toList());
            return super.mapUserPaginationResponseToDTO(result, userResponses);
        } else {
            return super.mapUserPaginationResponseToDTO(result, userResponseList);
        }
    }


    private ChallengesResponse mapChallengeDataBasedOnUserLanguage(Challenges challenge) {
        boolean isUserLanguageEn = userService.retrieveUserLanguage().equals(Language.EN);
        log.info("Is User Language is English --> {}", isUserLanguageEn);
        ChallengesResponse challengesResponse = challengesMapper.mapToResponse(challenge);
        this.applyChallengeSettingsResponseForChallengeResponse(challengesResponse, challenge);
        challengesResponse.setLocalTitle(
                isUserLanguageEn ?
                        challengesResponse.getTitleEn() : challengesResponse.getTitleAr());
        challengesResponse.setLocalDescription(
                isUserLanguageEn ?
                        challengesResponse.getDescriptionEn() : challengesResponse.getDescriptionAr());
        challengesResponse.setStatus(isUserLanguageEn ? challenge.getStatus().getEnglishName() : challenge.getStatus().getArabicName());
        if (Objects.nonNull(challenge.getVisibility()))
            challengesResponse.setVisibility(isUserLanguageEn ? challenge.getVisibility().getEnglishName() : challenge.getVisibility().getArabicName());
        challengesResponse.setModeratorGrpNumber(challenge.getModerator().getGrpNumber());
        return challengesResponse;
    }

    private void applyChallengeSettingsResponseForChallengeResponse(ChallengesResponse challengesResponse, Challenges challenges) {
        String fieldSettingsId = challenges.getField();
        String impactSettingsId = challenges.getImpact();
        String importanceOfDecisionSettingsId = challenges.getImportanceOfDecision();
        String levelOfDecisionSettingsId = challenges.getLevelOfDecision();
        String topicClassificationSettingsId = challenges.getTopicClassification();

        if (Objects.nonNull(fieldSettingsId) && checkNullOfRetrievingChallengeSettingsData(fieldSettingsId))
            challengesResponse.setFieldResponse(this.retrieveChallengeSettingsData(fieldSettingsId));

        if (Objects.nonNull(impactSettingsId) && checkNullOfRetrievingChallengeSettingsData(impactSettingsId))
            challengesResponse.setImpactResponse(this.retrieveChallengeSettingsData(impactSettingsId));

        if (Objects.nonNull(importanceOfDecisionSettingsId) && checkNullOfRetrievingChallengeSettingsData(importanceOfDecisionSettingsId))
            challengesResponse.setImportanceOfDecisionResponse(this.retrieveChallengeSettingsData(importanceOfDecisionSettingsId));

        if (Objects.nonNull(levelOfDecisionSettingsId) && checkNullOfRetrievingChallengeSettingsData(levelOfDecisionSettingsId))
            challengesResponse.setLevelOfDecisionResponse(this.retrieveChallengeSettingsData(levelOfDecisionSettingsId));

        if (Objects.nonNull(topicClassificationSettingsId) && checkNullOfRetrievingChallengeSettingsData(topicClassificationSettingsId))
            challengesResponse.setTopicClassificationResponse(this.retrieveChallengeSettingsData(topicClassificationSettingsId));
    }

    private ChallengeSettingsResponse retrieveChallengeSettingsData(String challengeSettingsId) {
        return challengeSettingsService.customFindByIdForChallenge(Integer.parseInt(challengeSettingsId));
    }

    private boolean checkNullOfRetrievingChallengeSettingsData(String challengeSettingsId) {
        return StringUtils.isNumeric(challengeSettingsId) && Objects.nonNull(challengeSettingsService.customFindByIdForChallenge(Integer.parseInt(challengeSettingsId)));
    }

    private void handleChallengesRelations(ChallengesCreationRequest request, Challenges challenges, Operations operations) {
        // Send an invitation reminder
        if (Objects.nonNull(request.getChallengeRemindersDateTimeList()) && !request.getChallengeRemindersDateTimeList().isEmpty())
            this.handleChallengeReminder(request.getChallengeRemindersDateTimeList(), challenges, operations);
        // Hashtags
        if (Objects.nonNull(request.getHashtagTexts()) && !request.getHashtagTexts().isEmpty())
            this.attachChallengeWithHashtags(request.getHashtagTexts(), challenges, operations);
        // Media mean (Documents & Video URLs)
        this.handleMediaRelations(request, challenges, operations);
        // handle saving solutions if exist
        if (Objects.nonNull(request.getSolutionsTexts()) && !request.getSolutionsTexts().isEmpty())
            this.handleSolutionsCreation(request.getSolutionsTexts(), challenges, operations);
        // Handle any User invitation (Validators Or Admin User Or Guest =)
        this.handleUserInvitations(request, challenges, operations);
    }

    private void handleSolutionsCreation(List<String> solutionsTexts, Challenges challenges, Operations operations) {
        if (challenges.getIsFinal() != 1 && !operations.equals(Operations.UPDATE))
            Optional.ofNullable(solutionsTexts)
                    .filter(texts -> !texts.isEmpty())
                    .ifPresent(texts -> solutionService.createSolutionForSpecificUserAndSolution(texts, challenges, operations));
    }

    private void handleMediaRelations(ChallengesCreationRequest request, Challenges challenges, Operations operations) {
        // Documents
        if (Objects.nonNull(request.getDocumentsIds()) && !request.getDocumentsIds().isEmpty())
            this.attachChallengeWithDocumentsAttached(request.getDocumentsIds(), challenges);
        // YouTube Or Videos Links
        if (Objects.nonNull(request.getYoutubeLinks()) && !request.getYoutubeLinks().isEmpty())
            this.attachChallengesWithVideoLinks(request.getYoutubeLinks(), challenges, operations);
    }

    private void handleUserInvitations(ChallengesCreationRequest request, Challenges challenges, Operations operations) {
        // UsersInvitations
        if (Objects.nonNull(request.getInvitedUsersIds()) && !request.getInvitedUsersIds().isEmpty())
            this.sendUsersInvitations(request, challenges, operations);
        // ValidatorsInvitations
        if (Objects.nonNull(request.getValidatorsIds()) && !request.getValidatorsIds().isEmpty())
            this.sendValidatorsInvitations(request.getValidatorsIds(), challenges, operations);
    }

    private void sendUsersInvitations(ChallengesCreationRequest request, Challenges challenges, Operations operations) {
        List<Long> invitedUsersIds = request.getInvitedUsersIds();
        if (Objects.nonNull(invitedUsersIds) && !invitedUsersIds.isEmpty()) {
            if (operations.equals(Operations.UPDATE))
                userInvitationService.removeAllByChallenge(challenges);
            invitedUsersIds.forEach(userGrp ->
                    userInvitationService.createUserInvitationChallengeInternal(userService.findUserByGrpNumber(userGrp), challenges,
                            DpDateUtil.convertStringsToLocalDateTimes(request.getChallengeRemindersDateTimeList())));
        }
    }

    private void sendValidatorsInvitations(List<Long> validatorsIds, Challenges challenges, Operations operations) {
        if (Objects.nonNull(validatorsIds) && !validatorsIds.isEmpty()) {
            if (operations.equals(Operations.UPDATE))
                validatorsService.removeAllByChallenge(challenges);
            validatorsService.create(
                    challenges, userService.findByGrpNumberInAndStatusAndRolesNameEn(validatorsIds, SystemRoles.VALIDATOR.getEn()));
        }
    }

    private void attachChallengeWithHashtags(List<String> hashtagTexts, Challenges challenges, Operations operations) {
        if (Objects.nonNull(hashtagTexts) && !hashtagTexts.isEmpty())
            challengeHashtagService.create(hashtagService.saveList(hashtagTexts), challenges, operations);
    }

    private void attachChallengeWithDocumentsAttached(List<Integer> documentsIdList, Challenges challenges) {
        if (Objects.nonNull(documentsIdList) && !documentsIdList.isEmpty()) {
            challengeDocumentsService.updateDocumentWithCreatedChallenge(documentsIdList, challenges);
        }
    }

    private void attachChallengesWithVideoLinks(List<String> linkList, Challenges challenges, Operations operations) {
        if (Objects.nonNull(linkList) && !linkList.isEmpty()) {
            if (operations.equals(Operations.UPDATE))
                challengeVideoService.removeAllByChallenge(challenges);
            challengeVideoService.create(linkList, challenges);
        }
    }

    private void handleChallengeReminder(List<String> challengeRemindersDateTimeList, Challenges challenges, Operations operations) {
        if (Objects.nonNull(challengeRemindersDateTimeList) && !challengeRemindersDateTimeList.isEmpty())
            challengeReminderService.create(challengeRemindersDateTimeList, challenges, operations);
    }

    private void handleModerator(ChallengesCreationRequest request, Challenges challenges) {
        challenges.setModerator(userService.findUserByGrpNumber(request.getModeratorId()));
        Long assistantModeratorGrp = request.getAssistantModeratorGrp();
        if (Objects.nonNull(assistantModeratorGrp))
            challenges.setAssistantModerator(userService.findUserByGrpNumber(assistantModeratorGrp));
    }

    private void handleMandatoryData(ChallengesCreationRequest request, Challenges challenges) {
        challenges.setStatus(request.isUrgent() ? ChallengeStatus.OPEN : ChallengeStatus.NEW);
        challenges.setStartTime(DpDateUtil.toLocalDateTime(request.getStartTime()));
        challenges.setEndTime(DpDateUtil.toLocalDateTime(request.getEndTime()));
        challenges.setSurveyStatus(SurveyStatus.NOT_STARTED);
        challenges.setStageList(new ArrayList<>());
        challenges.setIsSendSurvey(Boolean.FALSE);
        challenges.setCanAnswer(true);
        challenges.setJustView(false);
        challenges.setCanVote(true);
        challenges.setQueLevel(1);
        challenges.setIsFinal(0);
    }

    private void prepareStrategicToSave(ChallengesCreationRequest request, Challenges challenges) {
        Integer futureStrategicDirectionId = request.getFutureStrategicDirectionId();
        Integer futureStrategicGoalsId = request.getFutureStrategicGoalsId();
        Integer strategicDirectionId = request.getStrategicDirectionId();
        Integer strategicGoalsId = request.getStrategicGoalsId();

        if (Objects.nonNull(futureStrategicDirectionId))
            challenges.setFutureStrategicDirection(futureStrategicDirectionService.findById(futureStrategicDirectionId));
        if (Objects.nonNull(futureStrategicGoalsId))
            challenges.setFutureStrategicGoals(futureStrategicGoalsService.findById(futureStrategicGoalsId));
        if (Objects.nonNull(strategicDirectionId))
            challenges.setStrategicDirection(strategicDirectionService.findById(strategicDirectionId));
        if (Objects.nonNull(strategicGoalsId))
            challenges.setStrategicGoals(strategicGoalsService.findById(strategicGoalsId));
    }

    private ChallengesResponse prepareChallengeViewResponse(Challenges challenges) {
        ChallengesResponse response = mapChallengeDataBasedOnUserLanguage(challenges);
        response.setChallengeDocsResponseList(this.retrieveChallengeDocsDataResponse(challenges));
        response.setValidatorList(validatorsService.retrieveValidatorUsersByChallenge(challenges));
        response.setTeamMembersList(userInvitationService.findAllInvitedUsersByChallenges(challenges));
        response.setVotingResultResponse(votingService.retrieveSolutionVotingResult(challenges.getId()));
        response.setChatHistoryList(chatMessagesService.retrieveAllChatMessagesByChallengeId(challenges.getId()));
        response.setSolutionFullResponseList(solutionService.retrieveAllSolutionByChallengeId(challenges.getId(), null));
        response.setSolutionsPerStageResponseList(solutionService.retrieveAllSolutionByChallengePerStage(challenges));
        response.setUserInvitationCountedResponse(userInvitationService.retrieveCountResultForInvitedInternalUsers(challenges));
        response.setYoutubeUrls(challenges.getYoutube().isEmpty() ? new ArrayList<>() :
                challenges.getYoutube().stream()
                .map(ChallengeVideo::getLink)
                .collect(Collectors.toList()));
        response.setSolutionsTexts(
                (challenges.getIsFinal() == 1) ? null :
                        challenges.getSolutions().stream().map(Solution::getSolutionText).collect(Collectors.toList())
        );
        response.setCommitteeId(challenges.getCommitteeId());
        response.setHashtagTexts(challengeHashtagService.retrieveHashtagTextsByChallenges(challenges));
        List<SolutionFullResponse> solutionFullResponseList = response.getSolutionFullResponseList();
        response.setCountOfSolutions(solutionFullResponseList.size());
        response.setLastQueueLevel(
                solutionFullResponseList.stream()
                        .mapToInt(SolutionFullResponse::getQueLevel)
                        .max()
                        .orElse(0)
        );
        return response;
    }

    private List<ChallengeDocsResponse> retrieveChallengeDocsDataResponse(Challenges challenges) {
        return challengeDocumentsService.retrieveAllDocsByChallenge(challenges).stream().map(doc ->
                ChallengeDocsResponse.builder()
                        .id(doc.getId())
                        .createdAt(doc.getCreatedAt())
                        .createdBy(doc.getCreatedBy())
                        .updatedAt(doc.getUpdatedAt())
                        .updatedBy(doc.getUpdatedBy())
                        .content(doc.getDocumentFile())
                        .name(doc.getName())
                        .path(doc.getPath())
                        .type(doc.getType())
                        .build()
        ).collect(Collectors.toList());
    }

    private void checkChallengeRequestSettingsIds(ChallengesCreationRequest request) {
        // Extracting values from the request
        String fieldSettingsId = request.getField();
        String impactSettingsId = request.getImpact();
        String importanceOfDecisionSettingsId = request.getImportanceOfDecision();
        String levelOfDecisionSettingsId = request.getLevelOfDecision();
        String topicClassificationSettingsId = request.getTopicClassification();

        // Collecting non-null and non-blank IDs into a list
        List<String> validSettingsIds = Stream.of(
                        fieldSettingsId, impactSettingsId, importanceOfDecisionSettingsId,
                        levelOfDecisionSettingsId, topicClassificationSettingsId
                )
                .filter(id -> Objects.nonNull(id) && StringUtils.isNotBlank(id))
                .collect(Collectors.toList());

        // Handle non-null and non-blank settings IDs
        this.handleNotExistSettingsIds(validSettingsIds);
    }

    private boolean checkSettingsIdExistence(String settingsId) {
        return challengeSettingsService.existsById(Integer.parseInt(settingsId));
    }

    private void handleNotExistSettingsIds(List<String> settingsIdList) {
        settingsIdList.forEach(id -> {
            if (!checkSettingsIdExistence(id))
                throw new CustomNotFoundException(
                        super.translator.getMessage(NOT_FOUND, Arrays.asList(super.translator.getMessage(CHALLENGE_SETTINGS_TITLE), id)),
                        ErrorCodes.NOT_FOUND_ERR.getErrorCode());
        });
    }

    private List<SolutionResponse> prepareChallengeDashboardSolutionsBasedOnUserRole(Challenges challenge) {
        User user = userService.retrieveCurrentUser();
        Long grpNumber = user.getGrpNumber();
        boolean isModeratorOrAssistant = grpNumber.equals(challenge.getModerator().getGrpNumber()) ||
                (challenge.getAssistantModerator() != null && grpNumber.equals(challenge.getAssistantModerator().getGrpNumber()));
        List<Solution> solutionList = challenge.getSolutions();
        if (!solutionList.isEmpty()) {
            List<SolutionResponse> solutionResponses = solutionList.stream().map(this::updateResponseWithUserVotingStatus).collect(Collectors.toList());
            // Calculate the total votes
            int totalVotes = solutionResponses.stream().mapToInt(SolutionResponse::getVoteCount).sum();
            solutionResponses.forEach(res -> res.setFinalRate(((double) res.getVoteCount() / totalVotes) * 100));
            if (!challenge.getStatus().equals(ChallengeStatus.OPEN) || isModeratorOrAssistant) {
                return solutionResponses;
            } else {
                return solutionResponses.stream().filter(
                        solutionResponse -> Long.valueOf(
                                solutionResponse.getCreatedBy()).equals(grpNumber) ||
                                (Objects.nonNull(solutionResponse.getInitPublic()) && solutionResponse.getInitPublic().equals(Boolean.TRUE))
                ).collect(Collectors.toList());
            }
        } else {
            return new ArrayList<>();
        }
    }

    private SolutionResponse updateResponseWithUserVotingStatus(Solution solution) {
        SolutionResponse mappedSolutionResponse = modelMapper.map(solution, SolutionResponse.class);
        mappedSolutionResponse.setIsCurrentUserVoted(votingService.checkTheCurrentUserIsVoted(solution.getId()));
        return mappedSolutionResponse;
    }
}