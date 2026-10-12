/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgdsw.sdss.common.config.security.dto.UserPrincipal;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.utils.DpDateUtil;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.SystemRoles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.AUTHORIZATION;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.BEARER;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.GRP_NUMBER_NAME;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.GUID;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.IDM_ROLES;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.ROLE_LIST_NAME;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.ROLE_PREFIX;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.USERNAME;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expirationInMs}")
    private int jwtExpirationInMs;

    public String retrieveResolvedToken() {
        HttpServletRequest request = getRequest();
        assert request != null;
        return this.resolveToken(request);
    }

    public String generateToken(Authentication authentication) {
        String user = (String) authentication.getPrincipal();
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtExpirationInMs)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserGuidFromJWT(String token) {
        return retrieveClaims(token).get(GUID, String.class);
    }

    public String getUserGrpNumberFromJWT(String token) {
        return retrieveClaims(token).get(GRP_NUMBER_NAME, String.class);
    }

    public String getUsernameFromJWT(String token) {
        return retrieveClaims(token).get(USERNAME, String.class);
    }

    /**
     * Extracts the payload from the JWT token and maps it to a UserPrincipal object.
     *
     * @param token The JWT token.
     * @return A UserPrincipal object populated with data from the token.
     */
    public UserPrincipal getUserPrincipalFromToken(String token) {
        Claims claims = retrieveClaims(token);

        String userLangCode = claims.get("language", String.class);
        long userGrpNumber = Long.parseLong(claims.get(GRP_NUMBER_NAME, String.class));

        // Map claims to UserPrincipal fields
        return UserPrincipal.builder()
                .grpNumber(userGrpNumber)
                .employeeNumber(userGrpNumber)
                .sectorName(claims.get("sectorName", String.class))
                .sectorId(claims.get("sectorId", Integer.class))
                .username(claims.get("userName", String.class))
                .guid(claims.get("guid", String.class))
                .authorities(getAuthoritiesFromClaims(claims))
//                .authorities(mapRolesToTestAuthorities(Collections.singletonList("ROLE_MODERATOR"))) // TEST ONLY
                .email(claims.get("email", String.class)) // Retrieve email
                .ratings(claims.get("ratings", Boolean.class)) // Retrieve ratings
                .decision(claims.get("decision", Boolean.class)) // Retrieve a decision
                .fullName(claims.get("fullName", String.class)) // Retrieve fullName
                .phoneNumber(claims.get("phoneNumber", String.class)) // Retrieve phoneNumber
                .questionView(claims.get("questionView", Boolean.class)) // Retrieve questionView
                .chatContents(claims.get("chatContents", Boolean.class)) // Retrieve chatContents
                .scrollingText(claims.get("scrollingText", Boolean.class)) // Retrieve scrollingText
                .language(Language.fromString(userLangCode)) // Retrieve language
                .languageCode(userLangCode) // Retrieve language
                .socialMediaFeeds(claims.get("socialMediaFeeds", Boolean.class)) // Retrieve socialMediaFeeds
                .internalDocuments(claims.get("internalDocuments", Boolean.class)) // Retrieve internalDocuments
                .sub(claims.get("sub", String.class)) // Subject (User ID or Identifier)
                .iat(DpDateUtil.convertDateToLong(claims.get("iat", Date.class))) // Issued At (Token creation time)
                .exp(DpDateUtil.convertDateToLong(claims.get("exp", Date.class))) // Expiration Time
                .employeeName(claims.get("employeeName", String.class)) // Employee Name
                .departmentName(claims.get("departmentName", String.class)) // Department Name
                .departmentId(claims.get("departmentId", Integer.class)) // Department ID
                .gender(claims.get("gender", String.class)) // Gender
                .empType(claims.get("empType", Integer.class)) // Employee Type
                .gradeName(claims.get("gradeName", String.class)) // Grade Name
                .iss(claims.get("iss", String.class)) // Issuer
                .build();
    }

    public String generateToken(Claims claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtExpirationInMs)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Returns List<String>
    public List<String> getRoleKeys(Claims claims) {
        return getRawRoleList(claims).stream()
                .map(SystemRoles::fromTokenRole)
                .map(SystemRoles::getKey)
                .collect(Collectors.toList());
    }

    public List<String> getRoleNames(Claims claims) {
        return getRawRoleList(claims).stream()
                .map(SystemRoles::fromTokenRole)
                .map(SystemRoles::name)  // Assuming SystemRoles has getName() method
                .collect(Collectors.toList());
    }

    // Returns List<SimpleGrantedAuthority> (original version)
    public List<SimpleGrantedAuthority> mapRolesToAuthorities(Claims claims) {
        return getRoleKeys(claims).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    private List<String> getRawRoleList(Claims claims) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> roleList = mapper.convertValue(claims.get(IDM_ROLES),
                new TypeReference<List<String>>() {
                });
        return roleList == null ? Collections.emptyList() : roleList;
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        try {
            List<?> roleObjects = claims.get(IDM_ROLES, List.class);
            if (roleObjects == null || roleObjects.isEmpty()) {
                roleObjects = claims.get(ROLE_LIST_NAME, List.class);
            }

            return roleObjects.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(this::normalizeRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String normalizeRole(String role) {
        return role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role;
    }

    public boolean isExpired(String token) {
        return LocalDateTime.ofInstant(retrieveClaims(token).getExpiration().toInstant(), ZoneId.systemDefault()).isBefore(LocalDateTime.now(ZoneId.of("Asia/Dubai")));
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(7);
        }
        log.info("This a whitelisted URL...");
        return null;
    }

    public String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(7);
        }
        log.info("This a whitelisted URL.");
        return null;
    }

    public boolean validateToken(String authToken) {
        try {
            retrieveJwsClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            handleJwtException(ex);
            return false;
        }
    }

    private void handleJwtException(Exception ex) {
        final String errorDetail;
        final String logMessage;

        if (ex instanceof SignatureException) {
            errorDetail = " signature";
            logMessage = "Invalid JWT signature: {}";
        } else if (ex instanceof MalformedJwtException) {
            errorDetail = " token";
            logMessage = "Invalid JWT token: {}";
        } else if (ex instanceof ExpiredJwtException) {
            errorDetail = ", Token is Expired";
            logMessage = "Expired JWT token: {}";
        } else if (ex instanceof UnsupportedJwtException) {
            errorDetail = " Unsupported";
            logMessage = "Unsupported JWT token: {}";
        } else {
            errorDetail = " Unknown";
            logMessage = "Unknown JWT exception: {}";
        }

        log.error(logMessage, ex.getMessage());
        throw new CustomDataException(
                ErrorCodes.INVALID_JWT.getMessage() + errorDetail,
                ErrorCodes.INVALID_JWT.getErrorCode()
        );
    }

    public Claims retrieveClaims(String token) {
        return retrieveJwsClaims(token).getBody();
    }

    private Jws<Claims> retrieveJwsClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token);
    }

    /**
     * Return HTTP request
     *
     * @return HttpServletRequest
     */
    public HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        log.debug("Not called in the context of an HTTP request");
        return null;
    }
}