/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.base.scheduler.initializer;

import com.mgdsw.sdss.common.base.scheduler.SchedulerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */
@Service
@AllArgsConstructor
public class SchedulerInitializer {

    private final List<SchedulerService> schedulerServiceList;

    @PostConstruct
    public void initialize() {
        schedulerServiceList.forEach(SchedulerService::scheduleJobs);
    }
}
