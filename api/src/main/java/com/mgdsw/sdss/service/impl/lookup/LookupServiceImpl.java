/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.lookup;

import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.core.dto.DepartmentCoreResponse;
import com.mgdsw.sdss.core.service.DepartmentService;
import com.mgdsw.sdss.dto.challenge.ChallengeAdminDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeForUserDashboardResponse;
import com.mgdsw.sdss.dto.common.DropdownResponse;
import com.mgdsw.sdss.dto.lookup.ChallengeLookupCreationScreenResponse;
import com.mgdsw.sdss.dto.lookup.RolesLangResponse;
import com.mgdsw.sdss.dto.lookup.UserListLookupScreenResponse;
import com.mgdsw.sdss.dto.lookup.admin.AdminDashboardResponse;
import com.mgdsw.sdss.dto.lookup.admin.ChallengeDashboardResponse;
import com.mgdsw.sdss.dto.lookup.admin.DepartmentChallengeStatsResponse;
import com.mgdsw.sdss.dto.lookup.admin.UsersForAdminDashboardResponse;
import com.mgdsw.sdss.dto.lookup.home.MainHomeScreenResponse;
import com.mgdsw.sdss.dto.lookup.home.MainHomeSurveyScreenResponse;
import com.mgdsw.sdss.dto.lookup.user.TheUserDashboardResponse;
import com.mgdsw.sdss.dto.lookup.user.TheValidatorsDashboardResponse;
import com.mgdsw.sdss.dto.lookup.user.dashboard.MyMainDashboardResponse;
import com.mgdsw.sdss.dto.user.UserDashboardResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import com.mgdsw.sdss.enums.ChallengeSettingType;
import com.mgdsw.sdss.enums.ChallengeStatus;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.Status;
import com.mgdsw.sdss.enums.SystemRoles;
import com.mgdsw.sdss.enums.UserInvitationStatus;
import com.mgdsw.sdss.service.ChallengeSettingsService;
import com.mgdsw.sdss.service.ChallengesService;
import com.mgdsw.sdss.service.ExpertisesService;
import com.mgdsw.sdss.service.FutureStrategicDirectionService;
import com.mgdsw.sdss.service.FutureStrategicGoalsService;
import com.mgdsw.sdss.service.LookupService;
import com.mgdsw.sdss.service.RolesService;
import com.mgdsw.sdss.service.SdssSurveyService;
import com.mgdsw.sdss.service.SdssSurveyUsersService;
import com.mgdsw.sdss.service.StrategicDirectionService;
import com.mgdsw.sdss.service.StrategicGoalsService;
import com.mgdsw.sdss.service.UserInvitationService;
import com.mgdsw.sdss.service.UserService;
import com.mgdsw.sdss.service.ValidatorsService;
import com.mgdsw.sdss.service.VotingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class LookupServiceImpl implements LookupService {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final RolesService rolesService;
    private final VotingService votingService;
    private final SdssSurveyService sdssSurveyService;
    private final DepartmentService departmentService;
    private final ExpertisesService expertisesService;
    private final ChallengesService challengesService;
    private final ValidatorsService validatorsService;
    private final UserInvitationService userInvitationService;
    private final StrategicGoalsService strategicGoalsService;
    private final SdssSurveyUsersService sdssSurveyUsersService;
    private final ChallengeSettingsService challengeSettingsService;
    private final StrategicDirectionService strategicDirectionService;
    private final FutureStrategicGoalsService futureStrategicGoalsService;
    private final FutureStrategicDirectionService futureStrategicDirectionService;

    @Override
    @Transactional(readOnly = true)
    public UserListLookupScreenResponse getUserListScreenLookupData() {
        List<DepartmentCoreResponse> allDepartments = departmentService.getAllDepartments();
        return UserListLookupScreenResponse.builder()
                .departmentCoreResponseList(allDepartments)
                .roles(this.retrieveRoleBasedOnUserLanguage())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MyMainDashboardResponse myMainDashboard() {
        Set<String> authorities = DpCommonUtil.retrieveCurrentUserAuthorities();
        boolean isValidator = authorities.contains(SystemRoles.VALIDATOR.getKey());
        return MyMainDashboardResponse.builder()
                .userDashboardResponse(this.retrieveTheCurrentUserDashboardLookupData())
                .validatorsDashboardResponse(isValidator ? this.retrieveTheCurrentValidatorUserDashboardResponse() : null)
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ChallengeLookupCreationScreenResponse retrieveChallengeLookupCreationScreenResponse() {
        log.info("Retrieving challenge lookup creation screen response - use paginated APIs instead");
        return ChallengeLookupCreationScreenResponse.builder()
                .moderators(userService.findModeratorUsers())
                .expertisesResponseList(generateExpertisesResponseList())
                .strategicGoalsResponseList(generateStrategicGoalsResponseList())
                .strategicDirectionResponseList(generateStrategicDirectionResponseList())
                // settings
                .topicClassificationList(challengeSettingsService.findAllByType(ChallengeSettingType.TOPIC_CLASSIFICATION))
                .levelOfDecisionList(challengeSettingsService.findAllByType(ChallengeSettingType.LEVEL_OF_DECISION))
                .importanceOfDecisionList(challengeSettingsService.findAllByType(ChallengeSettingType.IMPORTANCE_OF_DECISION))
                .impactList(challengeSettingsService.findAllByType(ChallengeSettingType.IMPACT))
                .fieldList(challengeSettingsService.findAllByType(ChallengeSettingType.FIELD))
                .futureStrategicGoalsResponseList(futureStrategicGoalsService.getAll(DropdownResponse.class))
                .futureStrategicDirectionResponseList(futureStrategicDirectionService.getAll(DropdownResponse.class))
                .departmentResponseList(departmentService.getAllDepartments())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDropDownListResponse> retrieveTeamMembersBasedOnExpertises(String expertLevel, String areaOfExpertise) {
        return userService.getAllUsersWithPrimaryExpertisesSorted(expertLevel, areaOfExpertise);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse retrieveAdminDashboardLookupData() {
        Set<String> authorities = DpCommonUtil.retrieveCurrentUserAuthorities();
        boolean isCommander = authorities.contains(SystemRoles.COMMANDER.getKey());
        boolean isAdminOrModeratorOrCommander =
                isCommander ||
                        authorities.contains(SystemRoles.MODERATOR.getKey()) ||
                        authorities.contains(SystemRoles.ADMIN.getKey());
        log.info("isAdminOrModeratorOrCommander -> {}", isAdminOrModeratorOrCommander);
        return AdminDashboardResponse.builder()
                .isCommander(isCommander)
                .isModeratorOrAdminOrCommander(isAdminOrModeratorOrCommander)
                .userDashboardResponse(this.retrieveTheCurrentUserDashboardLookupData())
                .usersDashboardResponse(prepareUsersDashboardResponse(isAdminOrModeratorOrCommander))
                .challengeDashboardResponse(prepareChallengeDashboardResponse(isAdminOrModeratorOrCommander))
                .futureStrategicGoalsResponseList(challengesService.retrieveDashboardFutureStrategicGoalsResponse())
                .departmentChallengeStatsResponseList(getAllDepartmentsWithChallengeCount(isAdminOrModeratorOrCommander))
                .futureStrategicDirectionsResponseList(challengesService.retrieveDashboardFutureStrategicDirectionsResponse())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MainHomeScreenResponse retrieveHomePageStatistics() {
        return MainHomeScreenResponse.builder()
                .totalUsersOnTheSystemCount(userService.countAll())
                .totalExternalUsersCount(userService.countAllExternalUsers())
                .totalInternalUsersCount(userService.countAllInternalUsers())
                .totalChallengesCount(challengesService.countAllChallenges())
                .totalSurveysCount(sdssSurveyService.countAllSurveysExistOnTheSystem())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MainHomeSurveyScreenResponse retrieveSurveyHomePageStatistics() {
        return MainHomeSurveyScreenResponse.builder()
                .totalSurveysCount(sdssSurveyService.countAllSurveysExistOnTheSystem())
                .totalSurveysUsersOnTheSystemCount(sdssSurveyUsersService.countSurveyUsers())
                .build();
    }

    private List<DepartmentChallengeStatsResponse> getAllDepartmentsWithChallengeCount(boolean isAdminOrModeratorOrCommander) {
        return isAdminOrModeratorOrCommander ?
                departmentService.getAllDepartments().stream()
                .map(department -> {
                    Long challengeCount = challengesService.countByDepartmentId(String.valueOf(department.getId()));
                    return DepartmentChallengeStatsResponse.builder()
                           .shortName(department.getShortName())
                           .departmentName(department.getDepartmentName())
                           .departmentId(department.getId())
                           .challengeCount(challengeCount)
                           .build();
                })
                .collect(Collectors.toList()) : null;
    }

    private TheUserDashboardResponse retrieveTheCurrentUserDashboardLookupData() {
        return TheUserDashboardResponse.builder()
                .totalInvitationsCount(userInvitationService.countAllInvitationsForTheCurrentUser())
                .totalAcceptedCount(userInvitationService.countAllInvitationsForTheCurrentUserBasedOnStatus(UserInvitationStatus.ACCEPTED))
                .totalPendingCount(userInvitationService.countAllInvitationsForTheCurrentUserBasedOnStatus(UserInvitationStatus.PENDING))
                .totalRejectedCount(userInvitationService.countAllInvitationsForTheCurrentUserBasedOnStatus(UserInvitationStatus.REJECTED))

                .votingParticipatedCount(votingService.countTheNumberOfVotingForTheCurrentUser())
                .votingNotParticipatedCount(votingService.countTheNumberOfNotVotedForTheCurrentUser())

                .acceptedInvitationList(retrieveChallengesForTheCurrentUserBasedOnInvitationStatus(UserInvitationStatus.ACCEPTED))
                .rejectedInvitationsList(retrieveChallengesForTheCurrentUserBasedOnInvitationStatus(UserInvitationStatus.REJECTED))
                .pendingInvitationList(retrieveChallengesForTheCurrentUserBasedOnInvitationStatus(UserInvitationStatus.PENDING))
                .completedInvitationsList(retrieveChallengesForTheCurrentUserBasedOnChallengeStatus(ChallengeStatus.COMPLETED))
                .build();
    }

    private TheValidatorsDashboardResponse retrieveTheCurrentValidatorUserDashboardResponse() {
        return TheValidatorsDashboardResponse.builder()
                .totalInvitationsCount(validatorsService.countAllInvitationsForTheCurrentValidatorUser())
                .totalAcceptedCount(validatorsService.countAllInvitationsForTheCurrentValidatorUserBasedOnStatus(UserInvitationStatus.ACCEPTED))
                .totalPendingCount(validatorsService.countAllInvitationsForTheCurrentValidatorUserBasedOnStatus(UserInvitationStatus.PENDING))
                .totalRejectedCount(validatorsService.countAllInvitationsForTheCurrentValidatorUserBasedOnStatus(UserInvitationStatus.REJECTED))

                .acceptedInvitationList(retrieveChallengesForTheCurrentValidatorUserBasedOnInvitationStatus(UserInvitationStatus.ACCEPTED))
                .rejectedInvitationsList(retrieveChallengesForTheCurrentValidatorUserBasedOnInvitationStatus(UserInvitationStatus.REJECTED))
                .pendingInvitationList(retrieveChallengesForTheCurrentValidatorUserBasedOnInvitationStatus(UserInvitationStatus.PENDING))
                .completedInvitationsList(retrieveChallengesForTheCurrentValidatorUserBasedOnChallengeStatus(ChallengeStatus.COMPLETED))

                .requestsUnderReview(retrieveChallengesForTheCurrentValidatorUserBasedOnChallengeStatus(ChallengeStatus.UNDER_VALIDATOR_REVIEW))
                .reviewedChallengesList(retrieveChallengesForTheCurrentValidatorUserBasedOnChallengeStatus(ChallengeStatus.UNDER_ADMIN_REVIEW))
                .closedChallengesList(retrieveChallengesForTheCurrentValidatorUserBasedOnChallengeStatus(ChallengeStatus.CLOSED))
                .build();
    }

    private List<ChallengeForUserDashboardResponse> retrieveChallengesForTheCurrentUserBasedOnInvitationStatus(UserInvitationStatus status) {
        return userInvitationService.retrieveChallengesForTheCurrentUserBasedOnInvitationStatus(userService.retrieveCurrentUser(), status);
    }

    private List<ChallengeForUserDashboardResponse> retrieveChallengesForTheCurrentUserBasedOnChallengeStatus(ChallengeStatus status) {
        return userInvitationService.retrieveChallengesForTheCurrentUserBasedOnChallengeStatus(userService.retrieveCurrentUser(), status);
    }

    private List<ChallengeForUserDashboardResponse> retrieveChallengesForTheCurrentValidatorUserBasedOnInvitationStatus(UserInvitationStatus status) {
        return validatorsService.retrieveChallengesForTheCurrentValidatorUserBasedOnInvitationStatus(userService.retrieveCurrentUser(), status);
    }

    private List<ChallengeAdminDashboardResponse> retrieveChallengesForTheCurrentValidatorUserBasedOnChallengeStatus(ChallengeStatus status) {
        return validatorsService.retrieveChallengesForTheCurrentValidatorUserBasedOnChallengeStatus(userService.retrieveCurrentUser(), status);
    }

    private List<RolesLangResponse> retrieveRoleBasedOnUserLanguage() {
        Language language = userService.retrieveUserLanguage();
        log.info("User Language is -> {}", language);
        return rolesService.findAllActiveRoles().stream().map(role -> {
            RolesLangResponse rolesLangResponse = modelMapper.map(role, RolesLangResponse.class);
            rolesLangResponse.setRole(language.equals(Language.EN) ? role.getNameEn() : role.getNameAr());
            return rolesLangResponse;
        }).collect(Collectors.toList());
    }

    private List<DropdownResponse> generateExpertisesResponseList() {
        return expertisesService.getAll(DropdownResponse.class);
    }

    private List<DropdownResponse> generateStrategicGoalsResponseList() {
        return strategicGoalsService.getAll(DropdownResponse.class);
    }

    private List<DropdownResponse> generateStrategicDirectionResponseList() {
        try {
            return strategicDirectionService.getAll(DropdownResponse.class);
        } catch (Exception ex) {
            log.error("Failed to retrieve strategic direction lookup list", ex);
            return Collections.emptyList();
        }
    }

    private ChallengeDashboardResponse prepareChallengeDashboardResponse(boolean isAdminOrModeratorOrCommander) {
        return ChallengeDashboardResponse.builder()
                .underReviewCount(retrieveTheCountChallengesBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.UNDER_VALIDATOR_REVIEW))
                .urgentCount(retrieveTheCountChallengesBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.UNDER_ADMIN_REVIEW))
                .completedCount(retrieveTheCountChallengesBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.COMPLETED))
                .revisedCount(retrieveTheCountChallengesBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.REVISED))
                .closedCount(retrieveTheCountChallengesBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.CLOSED))
                .pausedCount(retrieveTheCountChallengesBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.PAUSED))
                .votingCount(retrieveTheCountChallengesBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.VOTING))
                .openCount(retrieveTheCountChallengesBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.OPEN))
                .newCount(retrieveTheCountChallengesBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.NEW))

                .totalChallenges(
                        isAdminOrModeratorOrCommander ?
                                challengesService.countAllChallenges() :
                                challengesService.countAllChallengesForCurrentUser()
                )

                .underReviewChallengesList(retrieveChallengesListResponseBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.UNDER_VALIDATOR_REVIEW))
                .urgentChallengesList(retrieveChallengesListResponseBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.UNDER_ADMIN_REVIEW))
                .completedChallengesList(retrieveChallengesListResponseBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.COMPLETED))
                .revisedChallengesList(retrieveChallengesListResponseBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.REVISED))
                .closedChallengesList(retrieveChallengesListResponseBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.CLOSED))
                .pausedChallengesList(retrieveChallengesListResponseBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.PAUSED))
                .votingChallengesList(retrieveChallengesListResponseBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.VOTING))
                .openChallengesList(retrieveChallengesListResponseBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.OPEN))
                .newChallengesList(retrieveChallengesListResponseBasedOnRoleAndStatus(isAdminOrModeratorOrCommander, ChallengeStatus.NEW))

                .challengesResponseList(
                        isAdminOrModeratorOrCommander ?
                                challengesService.retrieveAllChallenges() :
                                challengesService.retrieveAllChallengesForCurrentUser()
                )
                .build();
    }

    private UsersForAdminDashboardResponse prepareUsersDashboardResponse(boolean isAdminOrModerator) {
        return isAdminOrModerator ?
                UsersForAdminDashboardResponse.builder()
                .activeUsersCount(countUsersByStatus(Status.ACTIVE))
                .deletedUsersCount(countUsersByStatus(Status.DELETED))
                .notActiveUsersCount(countUsersByStatus(Status.INACTIVE))

                .externalUsersCount(userService.countAllExternalUsers())
                .internalUsersCount(userService.countAllInternalUsers())

                .totalUsersCount(userService.countAll())

                .activeUsersList(findAllUsersByStatus(Status.ACTIVE))
                .deletedUsersList(findAllUsersByStatus(Status.DELETED))
                .notActiveUsersList(findAllUsersByStatus(Status.INACTIVE))

                .internalAdminUsersList(userService.retrieveInternalUsers())
                .externalGuestUsersList(userService.retrieveExternalUsers())
                .build() : null;
    }

    private long retrieveTheCountChallengesBasedOnRoleAndStatus(boolean isAdminOrModeratorOrCommander, ChallengeStatus status) {
        return isAdminOrModeratorOrCommander ?
                countChallengesBasedOnStatusForCommander(status) :
                countChallengesBasedOnStatusForCurrentUser(status);
    }

    private List<ChallengeAdminDashboardResponse> retrieveChallengesListResponseBasedOnRoleAndStatus(boolean isAdminOrModeratorOrCommander, ChallengeStatus status) {
        return isAdminOrModeratorOrCommander ?
                retrieveChallengesListResponseBasedOnStatus(status) :
                retrieveChallengesListResponseBasedOnStatusForCurrentUser(status);
    }

    private List<ChallengeAdminDashboardResponse> retrieveChallengesListResponseBasedOnStatus(ChallengeStatus status) {
        return challengesService.retrieveAllChallengesBasedOnStatus(status);
    }

    private List<ChallengeAdminDashboardResponse> retrieveChallengesListResponseBasedOnStatusForCurrentUser(ChallengeStatus status) {
        return challengesService.retrieveAllChallengesBasedOnStatusForCurrentUser(status);
    }

    private long countChallengesBasedOnStatusForCommander(ChallengeStatus status) {
        return challengesService.countAllChallengesBasedOnStatus(status);
    }

    private long countChallengesBasedOnStatusForCurrentUser(ChallengeStatus status) {
        return challengesService.countAllChallengesBasedOnStatusForCurrentUser(status);
    }

    private long countUsersByStatus(Status status) {
        return userService.countAllByStatus(status);
    }

    private List<UserDashboardResponse> findAllUsersByStatus(Status status) {
        return userService.retrieveUsersByStatus(status);
    }
}
