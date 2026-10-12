/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.security;

import com.mgdsw.sdss.common.security.FileSecurityValidator;
import com.mgdsw.sdss.common.security.InputValidationUtil;
import com.mgdsw.sdss.common.security.SecureErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for security validation components
 *
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jan, 2025
 */
@Slf4j
@Configuration
public class FileSecurityConfig {

    @Bean
    public FileSecurityValidator fileSecurityValidator() {
        log.info("Initializing FileSecurityValidator for phishing and malicious content detection");
        return new FileSecurityValidator();
    }

    @Bean
    public InputValidationUtil inputValidationUtil() {
        log.info("Initializing InputValidationUtil for XSS and injection prevention");
        return new InputValidationUtil();
    }

    @Bean
    public SecureErrorHandler secureErrorHandler() {
        log.info("Initializing SecureErrorHandler for secure error message handling");
        return new SecureErrorHandler();
    }
}
