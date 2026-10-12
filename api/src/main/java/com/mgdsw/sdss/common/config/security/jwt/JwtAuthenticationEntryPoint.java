/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.helper.Translator;
import com.mgdsw.sdss.common.infrastructure.exception.CustomException;
import com.mgdsw.sdss.common.infrastructure.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.EXCEPTION;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.UTF8;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper;
    private Translator translator;

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException authException) throws IOException {
        // Set response status and content type
        httpServletResponse.setCharacterEncoding(UTF8);
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        prepareAuthExceptionResponse(httpServletRequest, httpServletResponse, authException);
    }

    private void prepareAuthExceptionResponse(HttpServletRequest httpServletRequest,
                                              HttpServletResponse httpServletResponse,
                                              AuthenticationException authException) throws IOException {
        // Check if the exception is stored in request attributes
        Exception exception = (Exception) httpServletRequest.getAttribute(EXCEPTION);

        if (Objects.nonNull(exception)) {
            if (exception instanceof CustomException) {
                handleAuthExceptionResponse(((CustomException) exception).getErrorCode(), exception.getMessage(), httpServletResponse);
            } else {
                handleAuthExceptionResponse(ErrorCodes.UNAUTHORIZED.getErrorCode(), authException.getMessage(), httpServletResponse);
            }
        } else {
            handleAuthExceptionResponse(LocalizedErrorCodes.UNAUTHORIZED.getErrorCode(), translator.getMessage(LocalizedErrorCodes.UNAUTHORIZED.getErrorCode(), new Locale("ar")), httpServletResponse);
        }
    }

    private void handleAuthExceptionResponse(String errorCode, String errorMessage,
                                             HttpServletResponse httpServletResponse) throws IOException {
        objectMapper.writeValue(httpServletResponse.getWriter(),
                ErrorResponse.builder()
                        .errorCode(errorCode)
                        .message(errorMessage)
                        .build()
        );
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setTranslator(Translator translator) {
        this.translator = translator;
    }
}