/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.config.security.app.AppConfiguration;
import com.mgdsw.sdss.common.config.security.jwt.JwtTokenProvider;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.common.utils.DpDateUtil;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.core.service.EmployeeService;
import com.mgdsw.sdss.dto.surveys.SurveyInvitationEmailSendResultResponse;
import com.mgdsw.sdss.email.service.EmailSender;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyInvitation;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.enums.SdssStatus;
import com.mgdsw.sdss.enums.SurveyStatus;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import com.mgdsw.sdss.helper.SurveyTemplateHelper;
import com.mgdsw.sdss.helper.template.FinalPreparationForSurveyTemplate;
import com.mgdsw.sdss.helper.template.SurveyTemplateRequest;
import com.mgdsw.sdss.quartz.ReminderEmailScheduler;
import com.mgdsw.sdss.quartz.surveys.scheduler.UpdateSdssSurveyStatusScheduler;
import com.mgdsw.sdss.repository.surveys.SdssSurveyInvitationRepository;
import com.mgdsw.sdss.repository.surveys.SdssSurveyRepository;
import com.mgdsw.sdss.repository.surveys.SdssSurveyRoundRepository;
import com.mgdsw.sdss.service.EmailTemplatesService;
import com.mgdsw.sdss.service.SdssSurveyInvitationService;
import com.mgdsw.sdss.service.SdssSurveyUsersService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.enums.TemplatesTypeKeys.SURVEY_INVITATION_KEY;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Log4j2
@Service
@AllArgsConstructor
public class SdssSurveyInvitationServiceImpl extends AbstractBaseServiceImpl<Integer, SdssSurveyInvitation> implements SdssSurveyInvitationService {

    private final SdssSurveyInvitationRepository repository;
    private final SdssSurveyRepository sdssSurveyRepository;
    private final SdssSurveyRoundRepository sdssSurveyRoundRepository;

    private final UpdateSdssSurveyStatusScheduler updateSdssSurveyStatusScheduler;
    private final ReminderEmailScheduler reminderEmailScheduler;
    private final EmailTemplatesService emailTemplatesService;
    private final SdssSurveyUsersService userSurveyService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppConfiguration appConfiguration;
    private final EmployeeService employeeService;
    private final EmailSender emailSender;
    private final Scheduler scheduler;

    @Override
    protected SdssSurveyInvitationRepository repository() {
        return repository;
    }


    @Override
    @Transactional
    public void initInvitation(TemplatesTypeKeys templatesTypeKey, SdssSurvey survey, SdssSurveyRound surveyRound) {
        this.createNewSurveyInvitationForEachUser(templatesTypeKey, survey, surveyRound);
    }

    @Override
    public void sendManualInvitation(SdssSurvey survey, SdssSurveyRound sdssSurveyRound) {
        HttpServletRequest request = jwtTokenProvider.getRequest();
        String baseUrl = Objects.nonNull(request) ? DpCommonUtil.getBaseUrl(request) : null;
        sendManualInvitation(survey, sdssSurveyRound, baseUrl);
    }

    @Override
    @Transactional
    public SurveyInvitationEmailSendResultResponse sendManualInvitationForUser(SdssSurvey survey, SdssSurveyRound sdssSurveyRound, Long grpNumber) {
        HttpServletRequest request = jwtTokenProvider.getRequest();
        String baseUrl = Objects.nonNull(request) ? DpCommonUtil.getBaseUrl(request) : null;
        return sendManualInvitationForUser(survey, sdssSurveyRound, grpNumber, baseUrl);
    }

    private void sendManualInvitation(SdssSurvey survey, SdssSurveyRound sdssSurveyRound, String baseUrl) {
        survey.getAllowedGrpList().forEach(grpNumber -> {
            try {
                sendManualInvitationForUser(survey, sdssSurveyRound, grpNumber, baseUrl);
            } catch (Exception ex) {
                log.error("Unexpected error while processing group [{}]: {}", grpNumber, ex.getMessage(), ex);
            }
        });
    }

    private SurveyInvitationEmailSendResultResponse sendManualInvitationForUser(SdssSurvey survey, SdssSurveyRound sdssSurveyRound, Long grpNumber, String baseUrl) {
        EmployeeInfoKeyResponse employeeInfo = null;
        try {
            employeeInfo = employeeService.retrieveInfoForEmployeeById(grpNumber);
            if (Objects.isNull(baseUrl)) {
                return buildEmailResult(grpNumber, employeeInfo, Boolean.FALSE,
                        "survey.invitation.email.notSent",
                        "survey.invitation.email.reason.missingBaseUrl");
            }

            List<SdssSurveyInvitation> existingInvitations = repository.findBySurveyRoundAndInvitedUser(
                    sdssSurveyRound,
                    userSurveyService.retrieveByGrpNumber(grpNumber));
            SdssSurveyInvitation invitation = existingInvitations.isEmpty() ?
                    this.createSingleInvitation(grpNumber, SURVEY_INVITATION_KEY, sdssSurveyRound, survey, baseUrl) :
                    existingInvitations.get(0);

            SurveyInvitationEmailSendResultResponse result = sendSurveyInvitationEmail(invitation, grpNumber, sdssSurveyRound);
            repository.save(invitation);
            return result;
        } catch (Exception e) {
            log.error("Unexpected error while sending invitation email for group [{}]: {}", grpNumber, e.getMessage(), e);
            return buildEmailResult(grpNumber, employeeInfo, Boolean.FALSE,
                    "survey.invitation.email.notSent",
                    "survey.invitation.email.reason.prepareFailed",
                    resolveFailureReason(e));
        }
    }

    @Override
    @Transactional
    public void sendManualInvitation(Integer surveyId, Integer roundId, String baseUrl) {
        SdssSurvey survey = sdssSurveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalStateException("Survey not found: " + surveyId));
        SdssSurveyRound round = sdssSurveyRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalStateException("Survey round not found: " + roundId));

        if (!surveyId.equals(round.getSurvey().getId())) {
            throw new IllegalStateException("Round " + roundId + " does not belong to survey " + surveyId);
        }

        sendManualInvitation(survey, round, baseUrl);
    }


    @Override
    public SdssSurveyInvitation checkUserExistenceForAttendingThisSurvey(SdssSurveyRound surveyRound, String userInfoKey) {
        List<SdssSurveyInvitation> invitations = repository.findBySurveyRoundAndInvitedUser(surveyRound, retrieveSdssSurveyUser(userInfoKey));
        if (invitations.isEmpty()) {
            throw new CustomAuthorizationException(
                    LocalizedErrorCodes.YOU_NOT_AUTHORIZED_TO_ENTER_THIS_SURVEY.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                    LocalizedErrorCodes.YOU_NOT_AUTHORIZED_TO_ENTER_THIS_SURVEY.getErrorCode());
        }
        // Return the most recently created invitation (using createdAt from BaseEntity)
        return invitations.stream()
                .max(Comparator.comparing(SdssSurveyInvitation::getCreatedAt))
                .orElseThrow(() -> new CustomAuthorizationException(
                        LocalizedErrorCodes.YOU_NOT_AUTHORIZED_TO_ENTER_THIS_SURVEY.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                        LocalizedErrorCodes.YOU_NOT_AUTHORIZED_TO_ENTER_THIS_SURVEY.getErrorCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public SdssSurveyInvitation checkUserExistenceForAttendingSurvey(SdssSurvey survey, String userInfoKey) {
        return repository.findBySurveyAndInvitedUser(survey, retrieveSdssSurveyUser(userInfoKey))
                .stream().findFirst()
                .orElseThrow(() ->
                        new CustomAuthorizationException(
                                LocalizedErrorCodes.YOU_NOT_AUTHORIZED_TO_ENTER_THIS_SURVEY.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()),
                                LocalizedErrorCodes.YOU_NOT_AUTHORIZED_TO_ENTER_THIS_SURVEY.getErrorCode())
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurvey> retrieveMyCurrentLiveSurveys(String userInfoKey) {
        SdssSurveyUsers surveyUser = retrieveSdssSurveyUser(userInfoKey);
        // Retrieve surveys where the user is invited
        return repository.findByInvitedUser(surveyUser)
                .stream()
                .map(SdssSurveyInvitation::getSurvey) // Map to surveys
                .filter(survey ->
                        survey.getStatus().equals(SurveyStatus.START) &&
                                survey.getRounds() != null &&
                                survey.getRounds().stream().anyMatch(round ->
                                        round.getStatus() == SdssStatus.IN_PROGRESS // Check if any round is IN_PROGRESS
                                )
                )
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurveyRound> retrieveMyCurrentHistorySurveys(String userInfoKey) {
        SdssSurveyUsers surveyUser = retrieveSdssSurveyUser(userInfoKey);
        return repository.findByInvitedUser(surveyUser)
                .stream().map(SdssSurveyInvitation::getSurveyRound).filter(
                        round -> round.getStatus().equals(SdssStatus.COMPLETED) && Boolean.FALSE.equals(round.getActive())
                ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countDistinctInvitedUsersBySurveyAndSurveyRound(SdssSurvey survey, SdssSurveyRound surveyRound) {
        return repository.countDistinctInvitedUsersBySurveyAndSurveyRound(survey, surveyRound);
    }

    private SdssSurveyUsers retrieveSdssSurveyUser(String userInfoKey) {
        return userSurveyService.retrieveByUserInfoKey(userInfoKey);
    }

    private void createNewSurveyInvitationForEachUser(TemplatesTypeKeys templatesTypeKey, SdssSurvey survey, SdssSurveyRound surveyRound) {
        // generate encoding for the employee info
        survey.getAllowedGrpList().stream().filter(grpNum ->
                Objects.nonNull(employeeService.findEmployeeByIdOrNull(grpNum))
        ).forEach(
                grpNumber -> {
                    SdssSurveyInvitation singleInvitation =
                            this.createSingleInvitation(grpNumber, templatesTypeKey, surveyRound, survey);
                    this.handleSchedulerProcessPerGrpUserForEachStage(grpNumber, singleInvitation, surveyRound);
                }
        );
    }


    private void handleSchedulerProcessPerGrpUserForEachStage(Long grpNumber,
                                                              SdssSurveyInvitation singleInvitation,
                                                              SdssSurveyRound surveyRound) {
        if (Boolean.FALSE.equals(surveyRound.getManualHandle())) {
            this.handleSchedulerProcessPerGrpUserForStartDate(grpNumber, singleInvitation, surveyRound);
            this.handleSchedulerProcessPerGrpUserForEndDate(grpNumber, singleInvitation, surveyRound);
        }
    }

    private void handleSchedulerProcessPerGrpUserForStartDate(Long grpNumber,
                                                              SdssSurveyInvitation singleInvitation,
                                                              SdssSurveyRound surveyRound) {
        handleSchedulerProcessPerGrpUser(grpNumber, singleInvitation, surveyRound, surveyRound.getStartTime());
    }

    private void handleSchedulerProcessPerGrpUserForEndDate(Long grpNumber,
                                                            SdssSurveyInvitation singleInvitation,
                                                            SdssSurveyRound surveyRound) {
        handleSchedulerProcessPerGrpUser(grpNumber, singleInvitation, surveyRound, surveyRound.getEndTime());
    }

    private void handleSchedulerProcessPerGrpUser(Long grpNumber,
                                                  SdssSurveyInvitation singleInvitation,
                                                  SdssSurveyRound surveyRound,
                                                  LocalDateTime dateTime) {
        try {
            ZoneId zoneId = ZoneId.of(appConfiguration.getTimeZone());
            ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, zoneId);
            DpDateUtil.validateThatTimeAfterNow(zonedDateTime, zoneId);
            SurveyTemplateRequest surveyTemplateRequest = this.prepareSurveyTemplateRequest(grpNumber, singleInvitation, surveyRound);
            JobDetail jobDetail = updateSdssSurveyStatusScheduler.buildReminderJobDetails(surveyTemplateRequest);
            Trigger trigger = reminderEmailScheduler.buildReminderTrigger(jobDetail, zonedDateTime);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException exception) {
            log.error("We have an exception while make a Scheduler for reminder emails and this issue is : {}.", exception.getMessage());
        }
    }

    private SdssSurveyInvitation createSingleInvitation(Long grpNumber, TemplatesTypeKeys templatesTypeKey,
                                                        SdssSurveyRound surveyRound, SdssSurvey survey) {
        HttpServletRequest request = jwtTokenProvider.getRequest();
        String baseUrl = Objects.nonNull(request) ? DpCommonUtil.getBaseUrl(request) : null;
        return createSingleInvitation(grpNumber, templatesTypeKey, surveyRound, survey, baseUrl);
    }

    private SdssSurveyInvitation createSingleInvitation(Long grpNumber, TemplatesTypeKeys templatesTypeKey,
                                                        SdssSurveyRound surveyRound, SdssSurvey survey,
                                                        String baseUrl) {
        if (Objects.isNull(baseUrl)) {
            throw new IllegalStateException("Base URL is required to create a survey invitation");
        }
        return repository.save(
                SdssSurveyInvitation.builder()
                        .inviteLink(baseUrl + appConfiguration.getMySurveyCheckURI() + survey.getId())
                        .invitedUser(userSurveyService.createInvitedUserInfo(grpNumber))
                        .templatesTypeKey(templatesTypeKey)
                        .surveyRound(surveyRound)
                        .sent(Boolean.FALSE)
                        .survey(survey)
                        .build()
        );
    }

    private EmailTemplates retrieveEmailTemplates(TemplatesTypeKeys templatesTypeKey) {
        return emailTemplatesService.retrieveActiveEmailTemplatesByTemplateType(templatesTypeKey.toString());
    }

    private SurveyTemplateRequest prepareSurveyTemplateRequest(Long grpNumber,
                                                               SdssSurveyInvitation surveyInvitation,
                                                               SdssSurveyRound surveyRound) {
        EmployeeInfoKeyResponse employeeInfoKeyResponse =
                userSurveyService.retrieveEmployeeInfoKeyResponse(grpNumber);
        SdssSurveyUsers sdssSurveyUsers = userSurveyService.retrieveByGrpNumber(grpNumber);
        EmailTemplates emailTemplates = this.retrieveEmailTemplates(surveyInvitation.getTemplatesTypeKey());
        return SurveyTemplateHelper.prepareSurveyTemplateRequest(
                FinalPreparationForSurveyTemplate.builder()
                        .emailTemplates(Objects.nonNull(emailTemplates) ? emailTemplates :
                                EmailTemplates.builder()
                                .subjectAr(SURVEY_INVITATION_KEY.getAr())
                                .subjectEn(SURVEY_INVITATION_KEY.getEn())
                                .build()
                        )
                        .surveyRound(surveyRound)
                        .invitedUser(sdssSurveyUsers)
                        .singleInvitation(surveyInvitation)
                        .employeeInfoKeyResponse(employeeInfoKeyResponse)
                        .build()
        );
    }

    private SurveyInvitationEmailSendResultResponse sendSurveyInvitationEmail(SdssSurveyInvitation invitation, Long grpNumber, SdssSurveyRound round) {
        EmployeeInfoKeyResponse employeeInfo = null;
        try {
            SurveyTemplateRequest templateRequest = prepareSurveyTemplateRequest(grpNumber, invitation, round);
            employeeInfo = templateRequest.getEmployeeInfoKeyResponse();
            if (Objects.isNull(templateRequest.getEmailList()) ||
                    Arrays.stream(templateRequest.getEmailList()).noneMatch(StringUtils::isNotBlank)) {
                invitation.setSent(Boolean.FALSE);
                return buildEmailResult(grpNumber, employeeInfo, Boolean.FALSE,
                        "survey.invitation.email.notSent",
                        "survey.invitation.email.reason.invalidEmail");
            }
            emailSender.sendEmail(SurveyTemplateHelper.prepareSurveyEmail(templateRequest));
            invitation.setSent(Boolean.TRUE); // email sent
            return buildEmailResult(grpNumber, employeeInfo, Boolean.TRUE,
                    "survey.invitation.email.sent",
                    "survey.invitation.email.reason.accepted");
        } catch (Exception e) {
            log.error("Failed to send email for group [{}]: {}", grpNumber, e.getMessage(), e);
            invitation.setSent(Boolean.FALSE); // failed to send
            return buildEmailResult(grpNumber, employeeInfo, Boolean.FALSE,
                    "survey.invitation.email.notSent",
                    "survey.invitation.email.reason.mailFailed",
                    resolveFailureReason(e));
        }
    }

    private SurveyInvitationEmailSendResultResponse buildEmailResult(Long grpNumber,
                                                                     EmployeeInfoKeyResponse employeeInfo,
                                                                     Boolean sent,
                                                                     String messageKey,
                                                                     String reasonKey) {
        return buildEmailResult(grpNumber, employeeInfo, sent, messageKey, reasonKey, Collections.emptyList());
    }

    private SurveyInvitationEmailSendResultResponse buildEmailResult(Long grpNumber,
                                                                     EmployeeInfoKeyResponse employeeInfo,
                                                                     Boolean sent,
                                                                     String messageKey,
                                                                     String reasonKey,
                                                                     List<Object> reasonArgs) {
        return SurveyInvitationEmailSendResultResponse.builder()
                .employeeGrpNumber(grpNumber)
                .fullName(Objects.nonNull(employeeInfo) ? employeeInfo.getFullName() : null)
                .emailAddress(Objects.nonNull(employeeInfo) ? employeeInfo.getEmailAddress() : null)
                .sent(sent)
                .message(translator.getMessage(messageKey))
                .reason(reasonArgs.isEmpty() ? translator.getMessage(reasonKey) : translator.getMessage(reasonKey, reasonArgs))
                .build();
    }

    private List<Object> resolveFailureReason(Exception exception) {
        String reason = StringUtils.isNotBlank(exception.getMessage()) ?
                exception.getMessage() :
                translator.getMessage("survey.invitation.email.reason.unexpected");
        return Collections.singletonList(reason);
    }
}
