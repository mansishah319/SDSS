/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.config.security.jwt.JwtTokenProvider;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.utils.CryptoUtil;
import com.mgdsw.sdss.core.dto.EmployeeInfoKeyResponse;
import com.mgdsw.sdss.core.service.EmployeeService;
import com.mgdsw.sdss.dto.surveys.user.ReportInvitedSurveyUserResponse;
import com.mgdsw.sdss.dto.surveys.user.ReportUserSurveyAnswerStatusResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import com.mgdsw.sdss.repository.surveys.SdssSurveyUsersRepository;
import com.mgdsw.sdss.service.SdssSurveyUserQuestionAnswersService;
import com.mgdsw.sdss.service.SdssSurveyUsersService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 26 Nov 2024
 */

@Service
public class SdssSurveyUsersServiceImpl extends AbstractBaseServiceImpl<Integer, SdssSurveyUsers> implements SdssSurveyUsersService {

    private final SdssSurveyUsersRepository repository;

    private final SdssSurveyUserQuestionAnswersService surveyUserQuestionAnswersService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmployeeService employeeService;
    private final HttpServletRequest request;
    private final CryptoUtil cryptoUtil;

    public SdssSurveyUsersServiceImpl(SdssSurveyUsersRepository repository,
                                      @Lazy SdssSurveyUserQuestionAnswersService surveyUserQuestionAnswersService,
                                      JwtTokenProvider jwtTokenProvider,
                                      CryptoUtil cryptoUtil,
                                      EmployeeService employeeService, HttpServletRequest request) {
        this.repository = repository;
        this.surveyUserQuestionAnswersService = surveyUserQuestionAnswersService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.employeeService = employeeService;
        this.request = request;
        this.cryptoUtil = cryptoUtil;
    }

    @Override
    protected SdssSurveyUsersRepository repository() {
        return repository;
    }

    @Override
    @Transactional
    public SdssSurveyUsers createInvitedUserInfo(Long employeeGrpNumber) {
        return repository.findByEmployeeGrpNumber(employeeGrpNumber).orElseGet(() -> {
            EmployeeInfoKeyResponse employeeInfoKeyResponse =
                    this.retrieveEmployeeInfoKeyResponseInternal(employeeGrpNumber);
            return repository.save(
                    SdssSurveyUsers.builder()
                            .employeeGrpNumber(employeeGrpNumber)
                            .employeeInfoKey(cryptoUtil.encrypt(employeeInfoKeyResponse))
                            .accessKey(UUID.randomUUID().toString())
                            .build()
            );
        });
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeInfoKeyResponse retrieveEmployeeInfoKeyResponse(Long grpNumber) {
        return retrieveEmployeeInfoKeyResponseInternal(grpNumber);
    }

    @Override
    public SdssSurveyUsers retrieveByGrpNumber(Long grpNumber) {
        return this.findByEmployeeGrpNumber(grpNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public SdssSurveyUsers retrieveByGrpNumberOrNull(Long grpNumber) {
        return repository.findByEmployeeGrpNumber(grpNumber).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public String loginSurveyAccessKey(String existUserToken, Long grpNumber, String accessKey) {
        if (Objects.nonNull(existUserToken) && jwtTokenProvider.validateToken(existUserToken)) {
            return handleIfTheUserIsSdssUser(existUserToken);
        }
        SdssSurveyUsers surveyUsers = this.findByEmployeeGrpNumber(grpNumber);
        if (!surveyUsers.getAccessKey().equals(accessKey)) {
            throw new CustomAuthorizationException(
                    String.format(LocalizedErrorCodes.YOUR_SURVEY_ACCESS_KEY_INCORRECT.getLocalizedMessage(request.getLocale()), accessKey),
                    LocalizedErrorCodes.YOUR_SURVEY_ACCESS_KEY_INCORRECT.getErrorCode());
        }
        return surveyUsers.getEmployeeInfoKey();
    }

    @Override
    @Transactional(readOnly = true)
    public SdssSurveyUsers retrieveByUserInfoKey(String userInfoKey) {
        return retrieveByUserInfoKeyInternal(userInfoKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportUserSurveyAnswerStatusResponse> getUsersWithAnswerStatus(SdssSurveyRound surveyRound) {
        List<SdssSurveyUsers> byEmployeeGrpNumberIn = repository.findByEmployeeGrpNumberIn(surveyRound.getSurvey().getAllowedGrpList());
        return byEmployeeGrpNumberIn.stream()
                .map(surveyUser ->
                        ReportUserSurveyAnswerStatusResponse.builder()
                                .reportUserResponse(
                                        ReportInvitedSurveyUserResponse.builder()
                                                .id(surveyUser.getId())
                                                .employeeGrpNumber(surveyUser.getEmployeeGrpNumber())
                                                .employeeInfoKey(surveyUser.getEmployeeInfoKey())
                                                .employeeInfo(cryptoUtil.decrypt(surveyUser.getEmployeeInfoKey(), EmployeeInfoKeyResponse.class))
                                                .build()
                                )
                                .hasAnswered(surveyUserQuestionAnswersService.checkIfUserHasAnswered(surveyUser.getId())) // Convert numeric result to Boolean
                                .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countSurveyUsers() {
        return repository.count();
    }

    private String handleIfTheUserIsSdssUser(String existUserToken) {
        long userGrpNumber = Long.parseLong(jwtTokenProvider.getUserGrpNumberFromJWT(existUserToken));
        SdssSurveyUsers surveyUsers = this.findByEmployeeGrpNumber(userGrpNumber);
        return surveyUsers.getEmployeeInfoKey();
    }

    private EmployeeInfoKeyResponse retrieveEmployeeInfoKeyResponseInternal(Long grpNumber) {
        return employeeService.retrieveInfoForEmployeeById(grpNumber);
    }

    private SdssSurveyUsers findByEmployeeGrpNumber(Long employeeGrpNumber) {
        Optional<SdssSurveyUsers> surveyUsersOptional = repository.findByEmployeeGrpNumber(employeeGrpNumber);
        if (surveyUsersOptional.isPresent()) {
            return surveyUsersOptional.get();
        } else {
            throw new CustomAuthorizationException(
                    String.format(LocalizedErrorCodes.YOUR_SURVEY_ACCESS_KEY_INCORRECT.getLocalizedMessage(request.getLocale()), employeeGrpNumber),
                    LocalizedErrorCodes.YOUR_SURVEY_ACCESS_KEY_INCORRECT.getErrorCode());
        }
    }

    private SdssSurveyUsers retrieveByUserInfoKeyInternal(String userInfoKey) {
        return repository.findByEmployeeInfoKey(userInfoKey)
                .orElseThrow(() -> new CustomAuthorizationException(
                        LocalizedErrorCodes.YOU_NOT_AUTHORIZED_TO_ENTER_THIS_SURVEY.getLocalizedMessage(request.getLocale()),
                        LocalizedErrorCodes.YOU_NOT_AUTHORIZED_TO_ENTER_THIS_SURVEY.getErrorCode())
                );
    }
}
