/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.helper;

import com.google.common.collect.ImmutableMap;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.email.dto.EmailDetails;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyInvitation;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import com.mgdsw.sdss.helper.template.FinalPreparationForSurveyTemplate;
import com.mgdsw.sdss.helper.template.SurveyTemplateRequest;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;
import static com.mgdsw.sdss.common.enums.Templates.SURVEY_INVITATION;
import static com.mgdsw.sdss.common.enums.Templates.SURVEY_LAST_STAGE_DONE;
import static com.mgdsw.sdss.common.enums.Templates.SURVEY_STAGE_DONE;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.CONTENT_AR;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.CONTENT_EN;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.SDSS_INVITED_USER;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.SDSS_SURVEY_ROUND;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.SURVEY;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.SURVEY_SESSION_LINK;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.USER;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Log4j2
public final class SurveyTemplateHelper {

    public static EmailDetails prepareSurveyEmail(SurveyTemplateRequest request) {
        return EmailDetails.builder()
                .templateName(determineSurveyPathByTemplateType(request.getSdssSurveyInvitation().getTemplatesTypeKey()))
                .model(retrieveMappedData(request))
                .subject(request.getSubjectEn())
                .to(request.getEmailList())
                .build();
    }

    /**
     * Retrieves a mapped set of data from the given SurveyTemplateRequest.
     *
     * @param templateRequest the request containing survey data (must not be null)
     * @return an immutable map of the provided data (survey, session link, etc.)
     * @throws IllegalArgumentException if the templateRequest is null
     */
    private static ImmutableMap<String, Object> retrieveMappedData(SurveyTemplateRequest templateRequest) {
        // Validate input parameter
        if (templateRequest == null) {
            throw new IllegalArgumentException("templateRequest must not be null");
        }

        // Initialize the map builder
        ImmutableMap.Builder<String, Object> modelBuilder = ImmutableMap.builder();

        // Retrieve fields from the request
        EmployeeInfoKeyResponse employeeInfoKeyResponse = templateRequest.getEmployeeInfoKeyResponse();
        SdssSurveyRound sdssSurveyRound = templateRequest.getSdssSurveyRound();
        String surveySessionLink = templateRequest.getSurveySessionLink();
        SdssSurveyUsers invitedUser = templateRequest.getInvitedUser();
        String contentEn = templateRequest.getContentEn();
        String contentAr = templateRequest.getContentAr();
        SdssSurvey survey = templateRequest.getSurvey();

        // Use Optional to handle null checks more concisely
        Optional.ofNullable(survey).ifPresent(s -> modelBuilder.put(SURVEY, s));
        Optional.ofNullable(contentEn).ifPresent(content -> modelBuilder.put(CONTENT_EN, content));
        Optional.ofNullable(contentAr).ifPresent(content -> modelBuilder.put(CONTENT_AR, content));
        Optional.ofNullable(sdssSurveyRound).ifPresent(round -> modelBuilder.put(SDSS_SURVEY_ROUND, round));
        Optional.ofNullable(surveySessionLink).ifPresent(link -> modelBuilder.put(SURVEY_SESSION_LINK, link));
        Optional.ofNullable(employeeInfoKeyResponse).ifPresent(user -> modelBuilder.put(USER, user));
        Optional.ofNullable(invitedUser).ifPresent(sdssInvitedUserSecInfo -> modelBuilder.put(SDSS_INVITED_USER, sdssInvitedUserSecInfo));

        // Build and return the immutable map
        return modelBuilder.build();
    }

    public static SurveyTemplateRequest prepareSurveyTemplateRequest(FinalPreparationForSurveyTemplate finalPreparationForSurveyTemplate) {
        EmployeeInfoKeyResponse employeeInfoKeyResponse = finalPreparationForSurveyTemplate.getEmployeeInfoKeyResponse();
        SdssSurveyInvitation singleInvitation = finalPreparationForSurveyTemplate.getSingleInvitation();
        EmailTemplates emailTemplates = finalPreparationForSurveyTemplate.getEmailTemplates();
        SdssSurveyRound surveyRound = finalPreparationForSurveyTemplate.getSurveyRound();
        SdssSurveyUsers invitedUser = finalPreparationForSurveyTemplate.getInvitedUser();
        SdssSurvey survey = singleInvitation.getSurvey();
        return SurveyTemplateRequest.builder()
                .emailList(new String[]{employeeInfoKeyResponse.getEmailAddress()})
                .surveySessionLink(singleInvitation.getInviteLink())
                .employeeInfoKeyResponse(employeeInfoKeyResponse)
                .subjectEn(emailTemplates.getSubjectEn())
                .subjectAr(emailTemplates.getSubjectAr())
                .contentEn(emailTemplates.getContentEn())
                .contentAr(emailTemplates.getContentAr())
                .sdssSurveyInvitation(singleInvitation)
                .sdssSurveyRound(surveyRound)
                .invitedUser(invitedUser)
                .survey(survey)
                .build();
    }

    public static String determineSurveyPathByTemplateType(TemplatesTypeKeys templatesTypeKeys) {
        switch (templatesTypeKeys) {
            case SURVEY_INVITATION_KEY:
                return SURVEY_INVITATION.getPath();
            case SURVEY_STAGE_DONE_KEY:
                return SURVEY_STAGE_DONE.getPath();
            case SURVEY_LAST_STAGE_DONE_KEY:
                return SURVEY_LAST_STAGE_DONE.getPath();
            default:
                throw new IllegalArgumentException("Unknown template type: " + templatesTypeKeys);
        }
    }

    private SurveyTemplateHelper() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
