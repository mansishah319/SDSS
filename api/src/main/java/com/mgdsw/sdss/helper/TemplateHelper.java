/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.helper;

import com.google.common.collect.ImmutableMap;
import com.mgdsw.sdss.email.dto.EmailDetails;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import com.mgdsw.sdss.helper.template.FinalPreparationForTemplateDto;
import com.mgdsw.sdss.helper.template.TemplateDto;
import com.mgdsw.sdss.util.InvitationUtil;
import com.mgdsw.sdss.util.UserUtils;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;
import static com.mgdsw.sdss.common.enums.Templates.CHALLENGE_USER_INVITATION_TEMPLATE;
import static com.mgdsw.sdss.common.enums.Templates.REMINDER_CHALLENGE_USER_INVITATION_TEMPLATE;
import static com.mgdsw.sdss.common.enums.Templates.VALIDATOR_CHALLENGE_INVITATION_TEMPLATE;
import static com.mgdsw.sdss.common.enums.Templates.VALIDATOR_CHALLENGE_NEW_TEMPLATE;
import static com.mgdsw.sdss.common.enums.Templates.VALIDATOR_NEW_VALIDATE_REMINDER_TEMPLATE;
import static com.mgdsw.sdss.common.enums.Templates.WELCOME_CHALLENGE_TEMPLATE;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.CHALLENGE;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.CHALLENGE_SESSION_LINK;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.CONTENT_AR;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.CONTENT_EN;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.INVITATION_URL;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.USER;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 9月, 2024
 */

@Log4j2
public final class TemplateHelper {

    public static EmailDetails prepareEmail(TemplateDto templateDto) {
        boolean isLanguageEnglish = templateDto.isLanguageEnglish();
        return EmailDetails.builder()
                .to(templateDto.getEmailList())
                .templateName(templateDto.getPath())
                .model(retrieveMappedData(templateDto))
                .subject(isLanguageEnglish ? templateDto.getSubjectEn() : templateDto.getSubjectAr())
                .build();
    }

    private static ImmutableMap<String, Object> retrieveMappedData(TemplateDto templateDto) {
        ImmutableMap.Builder<String, Object> modelBuilder = ImmutableMap.builder();
        User user = templateDto.getUser();
        Challenges challenge = templateDto.getChallenge();
        String inviteLink = templateDto.getInviteLink();
        String contentEn = templateDto.getContentEn();
        String contentAr = templateDto.getContentAr();
        String challengeSessionLink = templateDto.getChallengeSessionLink();
        if (Objects.nonNull(user))
            modelBuilder.put(USER, user);
        if (Objects.nonNull(challenge))
            modelBuilder.put(CHALLENGE, challenge);
        if (Objects.nonNull(inviteLink))
            modelBuilder.put(INVITATION_URL, inviteLink);
        if (Objects.nonNull(contentEn))
            modelBuilder.put(CONTENT_EN, contentEn);
        if (Objects.nonNull(contentAr))
            modelBuilder.put(CONTENT_AR, contentAr);
        if (Objects.nonNull(challengeSessionLink))
            modelBuilder.put(CHALLENGE_SESSION_LINK, challengeSessionLink);
        return modelBuilder.build();
    }

    public static String determinePathByTemplateType(TemplatesTypeKeys templatesTypeKeys) {
        switch (templatesTypeKeys) {
            case CHALLENGE_USER_INVITATION_KEY:
                return CHALLENGE_USER_INVITATION_TEMPLATE.getPath();
            case CHALLENGE_INVITATION_REMINDER_KEY:
                return REMINDER_CHALLENGE_USER_INVITATION_TEMPLATE.getPath();
            case VALIDATOR_INVITATION_KEY:
                return VALIDATOR_CHALLENGE_INVITATION_TEMPLATE.getPath();
            case VALIDATOR_CHALLENGE_NEW_KEY:
                return VALIDATOR_CHALLENGE_NEW_TEMPLATE.getPath();
            case WELCOME_KEY:
                return WELCOME_CHALLENGE_TEMPLATE.getPath();
            case VALIDATOR_NEW_VALIDATE_REMINDER_KEY:
                return VALIDATOR_NEW_VALIDATE_REMINDER_TEMPLATE.getPath();
            default:
                throw new IllegalArgumentException("Unknown template type: " + templatesTypeKeys);
        }
    }

    public static TemplateDto prepareEmailDetailsForChallenge(FinalPreparationForTemplateDto finalPreparation) {
        User user = finalPreparation.getUser();
        Challenges challenge = finalPreparation.getChallenge();
        EmailTemplates emailTemplates = finalPreparation.getEmailTemplates();
        boolean isUserLanguageEnglish = UserUtils.isUserLanguageEnglish(user);
        String[] receiverEmailsForTheUser = InvitationUtil.getReceiverEmailsForTheUser(user);
        return TemplateDto.builder()
                .challengeSessionLink(finalPreparation.getChallengeSessionLink())
                .inviteLink(finalPreparation.getInviteLink())
                .subjectEn(
                        TemplateHelper.retrieveEmailSubjectForChallenge(
                                Objects.nonNull(emailTemplates) ? emailTemplates.getSubjectEn() : finalPreparation.getDefaultSubjectEn(),
                                challenge.getTitleEn())
                )
                .subjectAr(
                        TemplateHelper.retrieveEmailSubjectForChallenge(
                                Objects.nonNull(emailTemplates) ? emailTemplates.getSubjectAr() : finalPreparation.getDefaultSubjectAr(),
                                challenge.getTitleAr())
                )
                .contentEn(Objects.nonNull(emailTemplates) ? emailTemplates.getContentEn() : null)
                .contentAr(Objects.nonNull(emailTemplates) ? emailTemplates.getContentAr() : null)
                .path(TemplateHelper.determinePathByTemplateType(finalPreparation.getTemplatesTypeKeys()))
                .isLanguageEnglish(isUserLanguageEnglish)
                .emailList(receiverEmailsForTheUser)
                .challenge(challenge)
                .user(user)
                .build();
    }

    public static String retrieveEmailSubjectForChallenge(String subject, String challengeTitle) {
        return (subject.contains("%s")) ? String.format(subject, challengeTitle) : subject;
    }

    private TemplateHelper() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
