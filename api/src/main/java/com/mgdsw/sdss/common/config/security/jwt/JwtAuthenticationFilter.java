/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.security.jwt;

import com.mgdsw.sdss.common.config.security.dto.UserPrincipal;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.CustomException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.Status;
import com.mgdsw.sdss.service.UserService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.EXCEPTION;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MDC_USER_ID_KEY;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider tokenProvider;
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("The requestURI is : {}", requestURI);
        if (Arrays.stream(DpConstants.Authentication.getWhiteListedPaths()).anyMatch(requestURI::contains)) {
            // Allow allowlisted requests to proceed without authentication
            log.info("This URI: {} is whitelisted URI", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        try {
            log.info("Secured requestURI is {}", requestURI);
            String jwt = tokenProvider.resolveToken(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt) && !tokenProvider.isExpired(jwt)) {

                UserPrincipal userPrincipal = this.prepareUserPrincipal(request, jwt);

                MDC.put(MDC_USER_ID_KEY, String.valueOf(userPrincipal.getGrpNumber()));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext newContext = SecurityContextHolder.createEmptyContext();
                newContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(newContext);
            } else {
                SecurityContextHolder.clearContext();
                logger.info("You Login to whitelisted Page!!!");
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            SecurityContextHolder.clearContext();
            if (ex instanceof CustomDataException)
                handleExceptionForTheJwtEntryPoint(request, ex);
            if (ex instanceof CustomAuthorizationException)
                throw new CustomAuthorizationException(ErrorCodes.UNAUTHORIZED.getMessage(), ErrorCodes.UNAUTHORIZED.getErrorCode());
            throw new BadCredentialsException(ErrorCodes.INVALID_TOKEN.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private UserPrincipal prepareUserPrincipal(HttpServletRequest request, String jwt) {
        try {
            UserPrincipal userPrincipal = tokenProvider.getUserPrincipalFromToken(jwt);
            Long grpNumber = userPrincipal.getGrpNumber();
            MDC.put(MDC_USER_ID_KEY, String.valueOf(grpNumber));
            log.info("grpNumber is {}", grpNumber);
            this.checkUserExistence(request, grpNumber);
            User userByGrpNumberAndStatus = userService.findUserByGrpNumberAndStatus(grpNumber, Status.ACTIVE);
            LocaleContextHolder.setLocale(new Locale(userByGrpNumberAndStatus.getLanguage().getCode()));
//            userPrincipal = UserPrincipal.createUserPrincipal(userByGrpNumberAndStatus) // TEST ONLY
            userPrincipal.setLanguage(userByGrpNumberAndStatus.getLanguage());
            log.info("userPrincipal Roles are {}", userPrincipal.getAuthorities());
            return userPrincipal;
        } catch (CustomException exception) {
            throw prepareCatchCustomAuthorizationException(request, exception.getMessage(), exception.getErrorCode());
        }
    }

    private void checkUserExistence(HttpServletRequest request, Long grpNumber) {
        if (!userService.isUserExistInSdssSystem(grpNumber)) {
            log.error("Kindly check this user grp Number {} is registered on system or not and contact with a moderator to add it because it is not authorized.", grpNumber);
            throw prepareCatchCustomAuthorizationException(request, ErrorCodes.SDSS_UNAUTHORIZED_USER.getMessage(), ErrorCodes.SDSS_UNAUTHORIZED_USER.getErrorCode());
        }
    }

    private CustomAuthorizationException prepareCatchCustomAuthorizationException(HttpServletRequest request, String exceptionMessage, String exceptionCode) {
        CustomAuthorizationException customAuthorizationException = new CustomAuthorizationException(exceptionMessage, exceptionCode);
        handleExceptionForTheJwtEntryPoint(request, customAuthorizationException);
        return customAuthorizationException;
    }

    private void handleExceptionForTheJwtEntryPoint(HttpServletRequest request, Exception ex) {
        request.setAttribute(EXCEPTION, ex);
    }

    @Autowired
    public void setTokenProvider(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}