/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.security.app;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Dec, 2023
 */

@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfiguration {

    private String validatorRequestsToValidate;
    private String challengeDashboardUri;
    private String serverMainContext;
    private String userDashboardUrl;
    private String mySurveyCheckURI;
    private String loginServiceName;
    private String timeZone;

    public ZoneId getZoneId() {
        return ZoneId.of(timeZone);
    }
}
