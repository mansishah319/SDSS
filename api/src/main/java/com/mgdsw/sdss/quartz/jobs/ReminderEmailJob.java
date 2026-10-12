/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.quartz.jobs;

import com.mgdsw.sdss.email.service.EmailSender;
import com.mgdsw.sdss.helper.TemplateHelper;
import com.mgdsw.sdss.helper.template.TemplateDto;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jul, 2024
 */

@Log4j2
@Component
public class ReminderEmailJob extends QuartzJobBean {

    private final EmailSender emailSender;

    public ReminderEmailJob(EmailSender emailSender) {
        this.emailSender = emailSender; // Constructor injection
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        TemplateDto templateDto = (TemplateDto) jobDataMap.get("templateDto");
        log.info("Template Path is : {}", templateDto.getPath());
        emailSender.sendEmail(TemplateHelper.prepareEmail(templateDto));
    }
}
