/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.quartz;

import com.mgdsw.sdss.helper.template.TemplateDto;
import com.mgdsw.sdss.quartz.jobs.ReminderEmailJob;
import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import static com.mgdsw.sdss.quartz.enums.JobGroups.REMINDER_INVITATION_EMAIL;
import static com.mgdsw.sdss.quartz.enums.JobTrigger.REMINDER_INVITATION_EMAIL_TRIGGER;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jul, 2024
 */

@Service
@RequiredArgsConstructor
public class ReminderEmailScheduler {

    public JobDetail buildReminderJobDetails(TemplateDto templateDto) {

        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("templateDto", templateDto);

        return JobBuilder.newJob(ReminderEmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), REMINDER_INVITATION_EMAIL.getJobName())
                .withDescription(REMINDER_INVITATION_EMAIL.getJobDescription())
                .usingJobData(jobDataMap)
                .storeDurably(true)
                .requestRecovery(true)
                .build();
    }

    public Trigger buildReminderTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), REMINDER_INVITATION_EMAIL_TRIGGER.getTriggerName())
                .withDescription(REMINDER_INVITATION_EMAIL_TRIGGER.getTriggerDescription())
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
