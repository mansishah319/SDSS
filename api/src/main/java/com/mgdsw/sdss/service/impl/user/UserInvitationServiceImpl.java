/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.user;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.config.security.app.AppConfiguration;
import com.mgdsw.sdss.common.config.security.jwt.JwtTokenProvider;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.common.utils.DpDateUtil;
import com.mgdsw.sdss.dto.challenge.ChallengeForUserDashboardResponse;
import com.mgdsw.sdss.dto.invitation.InvitationResponse;
import com.mgdsw.sdss.dto.invitation.UserInvitationCountResponse;
import com.mgdsw.sdss.dto.user.ChallengeUsersResponse;
import com.mgdsw.sdss.email.dto.EmailDetails;
import com.mgdsw.sdss.email.service.EmailSender;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.UserInvitation;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.enums.ChallengeInvitationType;
import com.mgdsw.sdss.enums.ChallengeStatus;
import com.mgdsw.sdss.enums.SystemRoles;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import com.mgdsw.sdss.enums.UserEmailInvitationStatus;
import com.mgdsw.sdss.enums.UserInvitationStatus;
import com.mgdsw.sdss.helper.TemplateHelper;
import com.mgdsw.sdss.helper.template.FinalPreparationForTemplateDto;
import com.mgdsw.sdss.helper.template.TemplateDto;
import com.mgdsw.sdss.quartz.ReminderEmailScheduler;
import com.mgdsw.sdss.repository.user.UserInvitationRepository;
import com.mgdsw.sdss.service.ChallengesService;
import com.mgdsw.sdss.service.EmailTemplatesService;
import com.mgdsw.sdss.service.UserInvitationService;
import com.mgdsw.sdss.service.UserService;
import com.mgdsw.sdss.util.RandomStringGenerator;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.enums.EmailSubject.CHALLENGE_INVITATION_REMINDER_SUBJECT;
import static com.mgdsw.sdss.common.enums.EmailSubject.CHALLENGE_USER_CREATION_SUBJECT;
import static com.mgdsw.sdss.enums.TemplatesTypeKeys.CHALLENGE_INVITATION_REMINDER_KEY;
import static com.mgdsw.sdss.enums.TemplatesTypeKeys.CHALLENGE_USER_INVITATION_KEY;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Log4j2
@Service
public class UserInvitationServiceImpl extends AbstractBaseServiceImpl<Integer, UserInvitation> implements UserInvitationService {

    private final UserInvitationRepository userInvitationRepository;
    private final ReminderEmailScheduler reminderEmailScheduler;
    private final EmailTemplatesService emailTemplatesService;
    private final ChallengesService challengesService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final UserService userService;
    private final Scheduler scheduler;
    private final AppConfiguration appConfiguration;

    public UserInvitationServiceImpl(UserInvitationRepository userInvitationRepository,
                                     ReminderEmailScheduler reminderEmailScheduler,
                                     EmailTemplatesService emailTemplatesService,
                                     @Lazy ChallengesService challengesService,
                                     JwtTokenProvider jwtTokenProvider,
                                     AppConfiguration appConfiguration,
                                     PasswordEncoder passwordEncoder,
                                     EmailSender emailSender,
                                     Scheduler scheduler,
                                     UserService userService) {
        this.userInvitationRepository = userInvitationRepository;
        this.reminderEmailScheduler = reminderEmailScheduler;
        this.emailTemplatesService = emailTemplatesService;
        this.challengesService = challengesService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.appConfiguration = appConfiguration;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.userService = userService;
        this.scheduler = scheduler;
    }

    @Override
    protected UserInvitationRepository repository() {
        return userInvitationRepository;
    }

    @Override
    @Transactional
    public void createUserInvitationChallengeInternal(User user, Challenges challenges, List<LocalDateTime> reminders) {
        userInvitationRepository.save(
                this.generateUserChallengeInvitation(user, challenges, ChallengeInvitationType.ADMIN_USER));
        // Challenge registration sends emails after the transaction commits, so the API response is not blocked.
    }

    @Override
    @Transactional
    public void sendPendingChallengeInvitations(Integer challengeId, List<String> reminderDateTimes) {
        Challenges challenge = this.retrieveChallengeById(challengeId);
        if (!challenge.getStatus().equals(ChallengeStatus.OPEN)) {
            return;
        }
        List<LocalDateTime> reminders = Objects.nonNull(reminderDateTimes) ?
                DpDateUtil.convertStringsToLocalDateTimes(reminderDateTimes) : null;
        userInvitationRepository.findAllByChallenges(challenge).forEach(userInvitation ->
                this.sendNotificationAndUpdateData(userInvitation.getUser(), challenge, userInvitation, reminders));
    }

    @Override
    public InvitationResponse createIndividualInvitation(String email, Integer challengeId, HttpServletRequest httpServletRequest) {
        User user = userService.findUserByEmail(email);
        Challenges challenges = this.retrieveChallengeById(challengeId);

        UserInvitation userInvitation = userInvitationRepository.save(
                this.generateUserChallengeInvitation(user, challenges, ChallengeInvitationType.ADMIN_TEMPORARY));
        this.sendNotificationAndUpdateData(user, challenges, userInvitation, null);
        return super.create(userInvitation, InvitationResponse.class);
    }

    @Override
    public List<InvitationResponse> createInvitationWhileSession(List<Long> userGrpList, Integer challengeId) {
        return userGrpList.stream().map(userGrp -> {
            User user = userService.findUserByGrpNumber(userGrp);
            Challenges challenges = this.retrieveChallengeById(challengeId);
            UserInvitation userInvitation = userInvitationRepository.save(
                    this.generateUserChallengeInvitation(user, challenges, ChallengeInvitationType.ADMIN_TEMPORARY));
            this.sendNotificationAndUpdateData(user, challenges, userInvitation, null);
            return super.create(userInvitation, InvitationResponse.class);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ChallengeUsersResponse> findAllInvitedUsersByChallenges(Challenges challenges) {
        List<UserInvitation> allByChallenges = userInvitationRepository.findAllByChallenges(challenges);
        return allByChallenges.stream().map(invitedUser ->
                super.modelMapper.map(invitedUser.getUser(), ChallengeUsersResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserInvitationCountResponse retrieveCountResultForInvitedInternalUsers(Challenges challenges) {
        return UserInvitationCountResponse.builder()
                .invitedInternalUserResponse(this.findAllInvitedUsersByChallenges(challenges))
                .rejectedUsersResponse(this.findAllInvitedUsersByChallengesBasedOnStatus(challenges, UserInvitationStatus.REJECTED))
                .acceptedUsersResponse(this.findAllInvitedUsersByChallengesBasedOnStatus(challenges, UserInvitationStatus.ACCEPTED))
                .pendingUsersResponse(this.findAllInvitedUsersByChallengesBasedOnStatus(challenges, UserInvitationStatus.PENDING))
                .countOfInvitationUsers(userInvitationRepository.countAllByChallenges(challenges))
                .countOfInvitationPending(userInvitationRepository.countAllByChallengesAndStatus(challenges, UserInvitationStatus.PENDING))
                .countOfInvitationAccepted(userInvitationRepository.countAllByChallengesAndStatus(challenges, UserInvitationStatus.ACCEPTED))
                .countOfInvitationRejected(userInvitationRepository.countAllByChallengesAndStatus(challenges, UserInvitationStatus.REJECTED))
                .build();
    }

    @Override
    public InvitationResponse updateInvitationStatus(Integer challengeId, String invitationStatus, String reason) {
        UserInvitation userInvitation = this.findInvitationForTheCurrentUser(this.retrieveChallengeById(challengeId));
        userInvitation.setStatus(UserInvitationStatus.valueOf(invitationStatus.toUpperCase()));
        userInvitation.setReason(Objects.nonNull(reason) ? reason : null);
        return modelMapper.map(userInvitationRepository.save(userInvitation), InvitationResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllInvitationsForTheCurrentUser() {
        return userInvitationRepository.countAllByUser(userService.retrieveCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllInvitationsForTheCurrentUserBasedOnStatus(UserInvitationStatus status) {
        return userInvitationRepository.countAllByUserAndStatus(userService.retrieveCurrentUser(), status);
    }

    @Override
    @Transactional
    public List<Challenges> findChallengesByUser(User user) {
        return this.findAllChallengesByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeForUserDashboardResponse> retrieveChallengesForTheCurrentUserBasedOnInvitationStatus(User user, UserInvitationStatus status) {
        return challengesService.prepareTheChallengesForTheCurrentUserDashboardBasedOnStatusResponse(this.findAllChallengesByUser(user, status), null, SystemRoles.MODERATOR);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeForUserDashboardResponse> retrieveChallengesForTheCurrentUserBasedOnChallengeStatus(User user, ChallengeStatus challengeStatus) {
        return challengesService.prepareTheChallengesForTheCurrentUserDashboardBasedOnStatusResponse(this.findAllChallengesByUser(user), challengeStatus, SystemRoles.MODERATOR);
    }

    @Override
    @Transactional
    public void removeAllByChallenge(Challenges challengeNeedToRemove) {
        userInvitationRepository.deleteByChallenges(challengeNeedToRemove);
    }

    @Override
    @Transactional(readOnly = true)
    public InvitationResponse retrieveTheCurrentUserInvitation(Challenges challenge) {
        UserInvitation invitationForTheCurrentUserWithoutExceptions = findInvitationForTheCurrentUserWithoutExceptions(challenge);
        return Objects.nonNull(invitationForTheCurrentUserWithoutExceptions) ? modelMapper.map(invitationForTheCurrentUserWithoutExceptions, InvitationResponse.class) : null;
    }

    private UserInvitation findInvitationForTheCurrentUser(Challenges challenge) {
        return userInvitationRepository.findByChallengesAndUser(challenge, userService.retrieveCurrentUser()).stream().findFirst()
                .orElseThrow(() ->
                        new CustomNotFoundException(
                                LocalizedErrorCodes.NOT_FOUND_EXCEPTION.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                                LocalizedErrorCodes.NOT_FOUND_EXCEPTION.getErrorCode()));
    }

    private UserInvitation findInvitationForTheCurrentUserWithoutExceptions(Challenges challenge) {
        return userInvitationRepository.findByChallengesAndUser(challenge, userService.retrieveCurrentUser()).stream().findFirst()
                .orElse(null);
    }

    private List<Challenges> findAllChallengesByUser(User user, UserInvitationStatus status) {
        return userInvitationRepository.findByUserAndStatus(user, status).stream()
                .map(UserInvitation::getChallenges)
                .collect(Collectors.toList());
    }

    private List<Challenges> findAllChallengesByUser(User user) {
        List<UserInvitation> userInvitations = userInvitationRepository.findAllByUser(user);
        return userInvitations.stream()
                .map(UserInvitation::getChallenges)
                .distinct().collect(Collectors.toList());
    }

    private UserInvitation generateUserChallengeInvitation(
            User user, Challenges challenges,
            ChallengeInvitationType inviteType
    ) {
        HttpServletRequest request = jwtTokenProvider.getRequest();
        log.info("Request BaseURL is --> {}", DpCommonUtil.getBaseUrl(request));
        String hashKey = passwordEncoder.encode(RandomStringGenerator.getAlphaNumericString(45));
        return UserInvitation.builder()
                .challengeSessionLink(DpCommonUtil.getBaseUrl(request) + appConfiguration.getChallengeDashboardUri() + challenges.getId())
                .inviteLink(DpCommonUtil.getBaseUrl(request) + appConfiguration.getUserDashboardUrl())
                .locale(DpCommonUtil.getSelectedLanguage())
                .status(UserInvitationStatus.PENDING)
                .isGuest(Boolean.FALSE)
                .challenges(challenges)
                .inviteType(inviteType)
                .hashKey(hashKey)
                .user(user)
                .build();
    }

    private void sendNotificationAndUpdateData(User user, Challenges challenges, UserInvitation userInvitation, List<LocalDateTime> reminders) {
        this.prepareInvitationsEmail(user, challenges, userInvitation, reminders);
        userInvitation.setId(userInvitation.getId());
        userInvitationRepository.save(userInvitation);
    }

    private void prepareInvitationsEmail(User user, Challenges challenge, UserInvitation userInvitation, List<LocalDateTime> reminders) {

        this.handleReminderInvitations(reminders, user, challenge, userInvitation);
        this.sendCreatedChallengeInvitations(user, challenge, userInvitation);
    }

    private void sendCreatedChallengeInvitations(User user, Challenges challenge, UserInvitation userInvitation) {
        try {
            EmailTemplates challengeCreationEmailTemplates =
                    emailTemplatesService.retrieveActiveEmailTemplatesByTemplateType(CHALLENGE_USER_INVITATION_KEY.toString());
            EmailDetails emailDetails = TemplateHelper.prepareEmail(
                    this.prepareEmailDetails(
                            challengeCreationEmailTemplates,
                            CHALLENGE_USER_CREATION_SUBJECT.getEn(),
                            CHALLENGE_USER_CREATION_SUBJECT.getAr(),
                            user, challenge, userInvitation,
                            CHALLENGE_USER_INVITATION_KEY
                    )
            );
            emailSender.sendEmail(emailDetails);
            userInvitation.setEmailInvitationStatus(UserEmailInvitationStatus.SENT);
        } catch (Exception e) {
            log.error("The email has this issue --> {}", e.getMessage());
            userInvitation.setEmailInvitationStatus(UserEmailInvitationStatus.NOT_SENT);
        } finally {
            userInvitation.setStatus(UserInvitationStatus.PENDING);
        }
    }

    private void handleReminderInvitations(List<LocalDateTime> reminders, User user, Challenges challenge, UserInvitation userInvitation) {
        try {
            EmailTemplates reminderEmailTemplates =
                    emailTemplatesService.retrieveActiveEmailTemplatesByTemplateType(CHALLENGE_INVITATION_REMINDER_KEY.toString());
            if (Objects.nonNull(reminders))
                reminders.forEach(reminderChallengeDateTime ->
                        this.prepareUserOneReminder(user, challenge, reminderChallengeDateTime, userInvitation, reminderEmailTemplates));
        } catch (Exception e) {
            log.error("Email has this issue --> {}", e.getMessage());
        }
    }

    private void prepareUserOneReminder(User user, Challenges challenge, LocalDateTime reminderTime,
                                        UserInvitation userInvitation, EmailTemplates reminderEmailTemplates) {
        try {

            ZoneId zoneId = ZoneId.of(appConfiguration.getTimeZone());
            ZonedDateTime zonedDateTime = ZonedDateTime.of(reminderTime, zoneId);
            DpDateUtil.validateThatTimeAfterNow(zonedDateTime, zoneId);
            TemplateDto emailDetails = this.prepareEmailDetails(
                    reminderEmailTemplates, CHALLENGE_INVITATION_REMINDER_SUBJECT.getEn(), CHALLENGE_INVITATION_REMINDER_SUBJECT.getAr(),
                    user, challenge, userInvitation, CHALLENGE_INVITATION_REMINDER_KEY
            );
            JobDetail jobDetail = reminderEmailScheduler.buildReminderJobDetails(
                    emailDetails
            );
            Trigger trigger = reminderEmailScheduler.buildReminderTrigger(jobDetail, zonedDateTime);
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("Email Reminders Created Successfully.");
        } catch (SchedulerException exception) {
            log.error("We have an exception while make a Scheduler for reminder emails and this issue is : {}.", exception.getMessage());
        }
    }

    private TemplateDto prepareEmailDetails(EmailTemplates emailTemplates,
                                            String defaultSubjectEn,
                                            String defaultSubjectAr,
                                            User user,
                                            Challenges challenge,
                                            UserInvitation userInvitation,
                                            TemplatesTypeKeys templatesTypeKeys) {
        return TemplateHelper.prepareEmailDetailsForChallenge(
                FinalPreparationForTemplateDto.builder()
                        .user(user)
                        .challenge(challenge)
                        .emailTemplates(emailTemplates)
                        .defaultSubjectAr(defaultSubjectAr)
                        .defaultSubjectEn(defaultSubjectEn)
                        .templatesTypeKeys(templatesTypeKeys)
                        .inviteLink(templatesTypeKeys.equals(CHALLENGE_USER_INVITATION_KEY) ? userInvitation.getInviteLink() : null)
                        .challengeSessionLink(templatesTypeKeys.equals(CHALLENGE_INVITATION_REMINDER_KEY) ? userInvitation.getChallengeSessionLink() : null)
                        .build()
        );
    }

    private Challenges retrieveChallengeById(Integer challengeId) {
        return challengesService.findById(challengeId);
    }

    private List<ChallengeUsersResponse> findAllInvitedUsersByChallengesBasedOnStatus(Challenges challenges, UserInvitationStatus status) {
        List<UserInvitation> allByChallenges = userInvitationRepository.findAllByChallengesAndStatus(challenges, status);
        return allByChallenges.stream().map(invitedUser ->
                super.modelMapper.map(invitedUser.getUser(), ChallengeUsersResponse.class)).collect(Collectors.toList());
    }
}