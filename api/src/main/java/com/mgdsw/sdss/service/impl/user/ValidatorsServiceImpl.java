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
import com.mgdsw.sdss.dto.challenge.ChallengeAdminDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeForUserDashboardResponse;
import com.mgdsw.sdss.dto.invitation.InvitationResponse;
import com.mgdsw.sdss.dto.user.ChallengeUsersResponse;
import com.mgdsw.sdss.email.service.EmailSender;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.Validators;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.enums.ChallengeInvitationType;
import com.mgdsw.sdss.enums.ChallengeStatus;
import com.mgdsw.sdss.enums.SystemRoles;
import com.mgdsw.sdss.enums.UserEmailInvitationStatus;
import com.mgdsw.sdss.enums.UserInvitationStatus;
import com.mgdsw.sdss.helper.TemplateHelper;
import com.mgdsw.sdss.helper.template.FinalPreparationForTemplateDto;
import com.mgdsw.sdss.repository.user.ValidatorsRepository;
import com.mgdsw.sdss.service.ChallengesService;
import com.mgdsw.sdss.service.EmailTemplatesService;
import com.mgdsw.sdss.service.UserService;
import com.mgdsw.sdss.service.ValidatorsService;
import com.mgdsw.sdss.util.RandomStringGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.enums.TemplatesTypeKeys.VALIDATOR_CHALLENGE_NEW_KEY;
import static com.mgdsw.sdss.enums.TemplatesTypeKeys.VALIDATOR_INVITATION_KEY;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Log4j2
@Service
public class ValidatorsServiceImpl extends AbstractBaseServiceImpl<Integer, Validators> implements ValidatorsService {

    private final ValidatorsRepository validatorsRepository;
    private final EmailTemplatesService emailTemplatesService;
    private final ChallengesService challengesService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final UserService userService;
    private final AppConfiguration appConfiguration;

    public ValidatorsServiceImpl(ValidatorsRepository validatorsRepository,
                                 EmailTemplatesService emailTemplatesService,
                                 @Lazy ChallengesService challengesService,
                                 JwtTokenProvider jwtTokenProvider,
                                 AppConfiguration appConfiguration,
                                 PasswordEncoder passwordEncoder,
                                 EmailSender emailSender,
                                 UserService userService) {
        this.emailTemplatesService = emailTemplatesService;
        this.validatorsRepository = validatorsRepository;
        this.challengesService = challengesService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.appConfiguration = appConfiguration;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.userService = userService;
    }

    @Override
    protected ValidatorsRepository repository() {
        return validatorsRepository;
    }

    @Override
    @Transactional
    public void create(Challenges challenges, List<User> userList) {
        List<Validators> validatorList = userList.stream().map(user ->
                challenges.getStatus().equals(ChallengeStatus.NEW) ?
                        this.generateNewValidators(user, challenges, ChallengeInvitationType.ADMIN_USER) :
                        this.generateNewValidators(user, challenges, ChallengeInvitationType.VALIDATION)
        ).collect(Collectors.toList());
        validatorsRepository.saveAll(validatorList);
    }

    @Override
    @Transactional
    public void sendPendingChallengeValidatorInvitations(Integer challengeId) {
        this.sendNotificationForAllValidators(validatorsRepository.findAllByChallenges(this.retrieveChallengeById(challengeId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllInvitedValidatorUsersByChallenges(Challenges challenges) {
        return validatorsRepository.findAllByChallenges(challenges).stream().map(Validators::getUser).collect(Collectors.toList());
    }

    @Override
    public List<ChallengeUsersResponse> retrieveValidatorUsersByChallenge(Challenges challenges) {
        return validatorsRepository.findByChallenges(challenges).stream().map(validator ->
                modelMapper.map(validator.getUser(), ChallengeUsersResponse.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllInvitationsForTheCurrentValidatorUser() {
        return validatorsRepository.countAllByUser(userService.retrieveCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllInvitationsForTheCurrentValidatorUserBasedOnStatus(UserInvitationStatus status) {
        return validatorsRepository.countAllByUserAndStatus(userService.retrieveCurrentUser(), status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeForUserDashboardResponse> retrieveChallengesForTheCurrentValidatorUserBasedOnInvitationStatus(User user, UserInvitationStatus status) {
        return challengesService.prepareTheChallengesForTheCurrentUserDashboardBasedOnStatusResponse(this.findAllChallengesByValidatorUser(user, status), null, SystemRoles.VALIDATOR);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeAdminDashboardResponse> retrieveChallengesForTheCurrentValidatorUserBasedOnChallengeStatus(User user, ChallengeStatus challengeStatus) {
        return challengesService.prepareTheChallengesForTheCurrentValidatorUserDashboardBasedOnStatusResponse(this.findAllChallengesByUser(user), challengeStatus);
    }

    @Override
    public InvitationResponse updateInvitationStatus(Integer challengeId, String invitationStatus, String reason) {
        Validators validators = validatorsRepository.findByChallengesAndUser(this.retrieveChallengeById(challengeId), userService.retrieveCurrentUser()).stream().findFirst()
                .orElseThrow(() ->
                        new CustomNotFoundException(
                                LocalizedErrorCodes.NOT_FOUND_EXCEPTION.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                                LocalizedErrorCodes.NOT_FOUND_EXCEPTION.getErrorCode()));
        validators.setStatus(UserInvitationStatus.valueOf(invitationStatus.toUpperCase()));
        validators.setReason(Objects.nonNull(reason) ? reason : null);
        return modelMapper.map(validatorsRepository.save(validators), InvitationResponse.class);
    }

    @Override
    public void removeAllByChallenge(Challenges challengeNeedToRemove) {
        validatorsRepository.deleteByChallenges(challengeNeedToRemove);
    }

    @Override
    public InvitationResponse retrieveTheCurrentValidatorInvitation(Challenges challenge) {
        Validators invitationForTheCurrentUserWithoutExceptions = findInvitationForTheCurrentUserWithoutExceptions(challenge);
        return Objects.nonNull(invitationForTheCurrentUserWithoutExceptions) ? modelMapper.map(invitationForTheCurrentUserWithoutExceptions, InvitationResponse.class) : null;
    }

    private List<Challenges> findAllChallengesByValidatorUser(User user, UserInvitationStatus status) {
        return validatorsRepository.findByUserAndStatus(user, status).stream()
                .map(Validators::getChallenges)
                .collect(Collectors.toList());
    }

    private List<Challenges> findAllChallengesByUser(User user) {
        List<Validators> userInvitations = validatorsRepository.findAllByUser(user);
        return userInvitations.stream()
                .map(Validators::getChallenges)
                .collect(Collectors.toList());
    }

    private Validators generateNewValidators(User user, Challenges challenges,
                                             ChallengeInvitationType inviteType) {
        String hashKey = passwordEncoder.encode(RandomStringGenerator.getAlphaNumericString(45));
        HttpServletRequest request = jwtTokenProvider.getRequest();
        log.info("Request Base URL is --> {}", DpCommonUtil.getBaseUrl(request));
        return Validators.builder()
                .user(user)
                .challenges(challenges)
                .hashKey(hashKey)
                .isSend(Boolean.FALSE)
                .inviteType(inviteType)
                .status(UserInvitationStatus.PENDING)
                .inviteLink(DpCommonUtil.getBaseUrl(request) + appConfiguration.getUserDashboardUrl())
                .build();
    }

    private void sendNotificationForAllValidators(List<Validators> validatorsList) {
        validatorsList.forEach(validator -> {
            Challenges challenges = validator.getChallenges();
            User user = validator.getUser();
            this.sendChallengeInvitationForUser(user, challenges, validator);
            this.updateValidatorAfterNotificationsSent(validator);
        });
    }

    private Challenges retrieveChallengeById(Integer challengeId) {
        return challengesService.findById(challengeId);
    }

    private void sendChallengeInvitationForUser(User user, Challenges challenge, Validators validators) {
        try {
            if (challenge.getStatus().equals(ChallengeStatus.NEW)) {
                this.sendEmailForValidatorsWithChallengeNew(user, challenge, validators);
            } else {
                this.sendEmailForValidatorsWithChallengeElseNew(user, challenge, validators);
            }
            validators.setIsSend(Boolean.TRUE);
            validators.setEmailInvitationStatus(UserEmailInvitationStatus.SENT);
        } catch (Exception e) {
            log.error("The email has this issue --> {}", e.getMessage());
            validators.setIsSend(Boolean.FALSE);
            validators.setEmailInvitationStatus(UserEmailInvitationStatus.NOT_SENT);
        } finally {
            validators.setStatus(UserInvitationStatus.PENDING);
        }
    }

    private void sendEmailForValidatorsWithChallengeNew(User user, Challenges challenge, Validators validators) {
        EmailTemplates emailTemplatesInvitationKey =
                emailTemplatesService.retrieveActiveEmailTemplatesByTemplateType(VALIDATOR_CHALLENGE_NEW_KEY.toString());
        emailSender.sendEmail(
                TemplateHelper.prepareEmail(
                        TemplateHelper.prepareEmailDetailsForChallenge(
                                FinalPreparationForTemplateDto.builder()
                                        .user(user)
                                        .challenge(challenge)
                                        .inviteLink(validators.getInviteLink())
                                        .emailTemplates(emailTemplatesInvitationKey)
                                        .templatesTypeKeys(VALIDATOR_CHALLENGE_NEW_KEY)
                                        .defaultSubjectAr(VALIDATOR_CHALLENGE_NEW_KEY.getAr())
                                        .defaultSubjectEn(VALIDATOR_CHALLENGE_NEW_KEY.getEn())
                                        .build()
                        )
                )
        );
    }

    private void sendEmailForValidatorsWithChallengeElseNew(User user, Challenges challenge, Validators validators) {
        EmailTemplates emailTemplatesInvitationKey =
                emailTemplatesService.retrieveActiveEmailTemplatesByTemplateType(VALIDATOR_INVITATION_KEY.toString());
        emailSender.sendEmail(
                TemplateHelper.prepareEmail(
                        TemplateHelper.prepareEmailDetailsForChallenge(
                                FinalPreparationForTemplateDto.builder()
                                        .user(user)
                                        .challenge(challenge)
                                        .inviteLink(validators.getInviteLink())
                                        .emailTemplates(emailTemplatesInvitationKey)
                                        .templatesTypeKeys(VALIDATOR_INVITATION_KEY)
                                        .defaultSubjectAr(VALIDATOR_INVITATION_KEY.getAr())
                                        .defaultSubjectEn(VALIDATOR_INVITATION_KEY.getEn())
                                        .build()
                        )
                )
        );
    }

    private void updateValidatorAfterNotificationsSent(Validators validator) {
        validator.setId(validator.getId());
        validator.setInviteType(ChallengeInvitationType.ADMIN_USER);
        validatorsRepository.save(validator);
    }

    private Validators findInvitationForTheCurrentUserWithoutExceptions(Challenges challenge) {
        return validatorsRepository.findByChallengesAndUser(challenge, userService.retrieveCurrentUser()).stream().findFirst()
                .orElse(null);
    }
}