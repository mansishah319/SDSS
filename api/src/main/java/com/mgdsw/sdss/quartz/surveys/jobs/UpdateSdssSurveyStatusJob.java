/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.quartz.surveys.jobs;

import com.mgdsw.sdss.dto.surveys.SdssSurveyResponse;
import com.mgdsw.sdss.dto.surveys.UpdateSdssSurveyStatus;
import com.mgdsw.sdss.email.service.EmailSender;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.enums.SdssStatus;
import com.mgdsw.sdss.enums.SurveyStatus;
import com.mgdsw.sdss.helper.SurveyTemplateHelper;
import com.mgdsw.sdss.helper.template.SurveyTemplateRequest;
import com.mgdsw.sdss.service.SdssSurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Log4j2
@Component
@RequiredArgsConstructor
public class UpdateSdssSurveyStatusJob extends QuartzJobBean {

    private final SdssSurveyService sdssSurveyService;
    private final EmailSender emailSender;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        SurveyTemplateRequest surveyTemplateReq = (SurveyTemplateRequest) jobDataMap.get("surveyTemplateReq");
        SdssSurvey survey = surveyTemplateReq.getSurvey();
        SdssSurveyRound sdssSurveyRound = surveyTemplateReq.getSdssSurveyRound();
        SdssSurveyResponse sdssSurveyResponse =
                sdssSurveyService.updateStatus(
                        UpdateSdssSurveyStatus.builder()
                                .roundNumber(sdssSurveyRound.getRoundNumber())
                                .surveyId(survey.getId())
                                .roundStatus(
                                        survey.getStatus().equals(SurveyStatus.NOT_STARTED) ?
                                                SdssStatus.IN_PROGRESS.getEn() :
                                                SdssStatus.COMPLETED.getEn()
                                )
                                .surveyStatus(
                                        sdssSurveyRound.getStatus().equals(SdssStatus.PENDING) ?
                                                SurveyStatus.START.getEn() :
                                                SurveyStatus.COMPLETED.getEn()
                                )
                                .manual(Boolean.FALSE)
                                .build()
                );

        log.info("The automatic Update Response for Survey is --> {}", sdssSurveyResponse);

        emailSender.sendEmail(SurveyTemplateHelper.prepareSurveyEmail(surveyTemplateReq));
    }
}
