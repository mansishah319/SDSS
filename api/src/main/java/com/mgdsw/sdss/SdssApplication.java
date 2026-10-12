/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss;

import com.mgdsw.sdss.common.config.security.app.AppConfiguration;
import com.mgdsw.sdss.common.config.security.app.SecurityAppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties({SecurityAppConfig.class, AppConfiguration.class})
public class SdssApplication {

    public static void main(String[] args) {
        SpringApplication.run(SdssApplication.class, args);
    }

}
