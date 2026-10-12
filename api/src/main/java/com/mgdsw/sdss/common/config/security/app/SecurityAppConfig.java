/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.security.app;

import com.mgdsw.sdss.common.config.security.app.dto.Cipher;
import com.mgdsw.sdss.common.config.security.app.dto.Encryption;
import com.mgdsw.sdss.common.config.security.app.dto.Jwt;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityAppConfig {

    private String aesEncryptionKey;
    private Encryption encryption;
    private Cipher cipher;
    private Jwt jwt;
}
