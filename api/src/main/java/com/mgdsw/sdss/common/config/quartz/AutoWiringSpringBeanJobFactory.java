/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.quartz;

import io.micrometer.core.lang.NonNullApi;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2024
 */

@Component
@NonNullApi
@RequiredArgsConstructor
public final class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory {

    private final ApplicationContext applicationContext;

    @Override
    protected Object createJobInstance(@NonNull TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        // Autowire the job using Spring’s application context
        applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
        return job;
    }
}
