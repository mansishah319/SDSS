/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.core.client;

import com.mgdsw.sdss.common.config.security.dto.UserPrincipal;
import com.mgdsw.sdss.common.config.security.jwt.JwtTokenProvider;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomServiceException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.core.dto.login.LoginRequest;
import com.mgdsw.sdss.core.dto.login.SdssLoginUserResponse;
import com.mgdsw.sdss.core.service.EmployeePhotoService;
import com.mgdsw.sdss.entity.user.Roles;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.Status;
import com.mgdsw.sdss.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.ROLES;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.ROLE_LIST_NAME;
import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.ROLE_PREFIX;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SLASH;
import static org.apache.commons.codec.CharEncoding.UTF_8;


/**
 * -----------------------------------------------------
 * * Not used anymore (This is the old scenario for login)
 * -----------------------------------------------------
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 09 Jan 2025
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class CoreWebClient {

    @Value("${services.core.urls.core-service}")
    private String coreServiceUrl;
    @Value("${services.core.uris.login_uri}")
    private String loginUri;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final EmployeePhotoService employeePhotoService;

    @Transactional
    public SdssLoginUserResponse loginToNtlm(LoginRequest request) {
        return loginInternal(request, this::retrieveStandardClaims);
    }

    @Transactional
    public SdssLoginUserResponse loginToNtlmWithExtendedClaims(LoginRequest request) {
        return loginInternal(request, this::retrieveExtendedClaims);
    }

    private SdssLoginUserResponse loginInternal(LoginRequest request, Function<String, Claims> claimsRetriever) {
        String url = coreServiceUrl + loginUri;

        if (Objects.nonNull(request.getGrpNumber()))
            url = url + SLASH + request.getGrpNumber();

        url = appendLocalTestDataHandler(url, request);

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(request.getUsername(), request.getPassword(), "SDSS_PORTAL", ""));

        try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build()) {
            HttpGet httpGet = new HttpGet(url);

            StringEntity input = new StringEntity(UTF_8);
            input.setContentEncoding(UTF_8);
            httpGet.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            httpGet.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                throw new CustomAuthorizationException(
                        LocalizedErrorCodes.INVALID_CREDENTIALS.getLocalizedMessage(DpCommonUtil.getCurrentUserLocaleForPublicApis()),
                        LocalizedErrorCodes.INVALID_CREDENTIALS.getErrorCode()
                );
            }

            String userToken = EntityUtils.toString(response.getEntity(), UTF_8);
            String userGrpNumberFromJWT = tokenProvider.getUserGrpNumberFromJWT(userToken);
            User employee = userService.findUserByGrpNumberAndStatus(Long.parseLong(userGrpNumberFromJWT), Status.ACTIVE, Language.AR);

            // Generate a new token with updated claims
            String updatedToken = tokenProvider.generateToken(claimsRetriever.apply(userToken));

            UserPrincipal userPrincipal = tokenProvider.getUserPrincipalFromToken(updatedToken);

            return buildLoginResponse(updatedToken, userPrincipal, employee);
        } catch (ConnectException e) {
            throw new CustomServiceException(
                    LocalizedErrorCodes.LOGIN_SERVICE_DOWN_MESSAGE.getLocalizedMessage(DpCommonUtil.getCurrentUserLocaleForPublicApis()),
                    LocalizedErrorCodes.LOGIN_SERVICE_DOWN_MESSAGE.getErrorCode());
        } catch (IOException e) {
            throw new CustomAuthorizationException(
                    LocalizedErrorCodes.LOGIN_WRONG_INFO_MESSAGE.getLocalizedMessage(DpCommonUtil.getCurrentUserLocaleForPublicApis()),
                    LocalizedErrorCodes.LOGIN_WRONG_INFO_MESSAGE.getErrorCode());
        }
    }

    private String appendLocalTestDataHandler(String url, LoginRequest loginRequest) {
        if (!"local".equalsIgnoreCase(activeProfile) || loginRequest == null) {
            return url;
        }

        try {
            List<String> systemRoles = loginRequest.getSystemRoles();
            URIBuilder uriBuilder = new URIBuilder(url);

            if (systemRoles != null && !systemRoles.isEmpty()) {
                systemRoles.stream()
                        .filter(role -> role != null && !role.trim().isEmpty())
                        .forEach(role -> uriBuilder.addParameter("roles", role));
            }
            uriBuilder.addParameter("username", loginRequest.getUsername());

            return uriBuilder.build().toString();
        } catch (URISyntaxException ex) {
            log.warn("Unable to append roles query params, using original url", ex);
            return url;
        }
    }

    private SdssLoginUserResponse buildLoginResponse(String token, UserPrincipal userPrincipal, User employee) {
        return SdssLoginUserResponse.builder()
                .token(token)
                .exp(userPrincipal.getExp())
                .iat(userPrincipal.getIat())
                .gradeName(userPrincipal.getGradeName())
                .iss(userPrincipal.getIss())
                .phoneNumber(userPrincipal.getPhoneNumber())
                .employeeName(userPrincipal.getEmployeeName())
                .userName(userPrincipal.getUsername())
                .sub(userPrincipal.getSub())
                .empType(userPrincipal.getEmpType())
                .gender(userPrincipal.getGender())
                .sectorId(userPrincipal.getSectorId())
                .sectorName(userPrincipal.getSectorName())
                .departmentId(userPrincipal.getDepartmentId())
                .departmentName(userPrincipal.getDepartmentName())
                .guid(userPrincipal.getGuid())
                .ratings(employee.getRatings())
                .email(userPrincipal.getEmail())
                .decision(employee.getDecision())
                .fullName(employee.getFullName())
                .grpNumber(userPrincipal.getGrpNumber())
                .questionView(employee.getQuestionView())
                .chatContents(employee.getChatContents())
                .scrollingText(employee.getScrollingText())
                .language(employee.getLanguage().getName())
                .languageCode(userPrincipal.getLanguageCode())
                .socialMediaFeeds(employee.getSocialMediaFeeds())
                .internalDocuments(employee.getInternalDocuments())
                .userPhoto(employeePhotoService.retrieveEmployeeImageByGrpNumber(employee.getGrpNumber()))
                .roles(userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(role -> role.replaceFirst("^ROLE_", ""))
                        .collect(Collectors.toList()))
                .build();
    }

    private Claims retrieveStandardClaims(String userToken) {
        User employee = getUserFromToken(userToken);
        Claims notStgClaims = retrieveSharedClaims(userToken, employee);
        notStgClaims.put(ROLE_LIST_NAME, tokenProvider.getRoleKeys(notStgClaims));
        notStgClaims.put(ROLES, tokenProvider.getRoleNames(notStgClaims));
        return notStgClaims;
    }

    private Claims retrieveExtendedClaims(String userToken) {
        User employee = getUserFromToken(userToken);
        Claims claims = retrieveSharedClaims(userToken, employee);

        // Additional extended claims
        claims.put(ROLE_LIST_NAME, employee.getRoles().stream().map(role -> ROLE_PREFIX + role.getNameEn()).collect(Collectors.toList()));
        claims.put(ROLES, employee.getRoles().stream().map(Roles::getNameEn).collect(Collectors.toList()));

        return claims;
    }

    private User getUserFromToken(String userToken) {
        String userGrpNumberFromJWT = tokenProvider.getUserGrpNumberFromJWT(userToken);
        return userService.findUserByGrpNumberAndStatus(Long.parseLong(userGrpNumberFromJWT), Status.ACTIVE, Language.AR);
    }

    private String getIssuerBasedOnEnv(String activeProfile) {
        switch (activeProfile) {
            case "dev":
                return "DEV_ISSUER";
            case "stg":
                return "STG_ISSUER";
            case "prod":
                return "PROD_ISSUER";
            default:
                return "DEFAULT_ISSUER";
        }
    }

    private Claims retrieveSharedClaims(String userToken, User employee) {
        Claims claims = tokenProvider.retrieveClaims(userToken);
        claims.setIssuer(getIssuerBasedOnEnv(activeProfile));
        claims.put("email", employee.getEmail());
        claims.put("ratings", employee.getRatings());
        claims.put("decision", employee.getDecision());
        claims.put("fullName", employee.getFullName());
        claims.put("phoneNumber", employee.getMobile());
        claims.put("questionView", employee.getQuestionView());
        claims.put("chatContents", employee.getChatContents());
        claims.put("scrollingText", employee.getScrollingText());
        claims.put("language", employee.getLanguage().getCode());
        claims.put("socialMediaFeeds", employee.getSocialMediaFeeds());
        claims.put("internalDocuments", employee.getInternalDocuments());
        claims.put("isDelphiModerator", employee.getIsDelphiModerator());

        return claims;
    }
}
