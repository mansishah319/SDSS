/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeAdminDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeAttendeesResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeForUserDashboardResponse;
import com.mgdsw.sdss.dto.challenge.ChallengeSearchRequest;
import com.mgdsw.sdss.dto.challenge.ChallengesCreationRequest;
import com.mgdsw.sdss.dto.challenge.ChallengesResponse;
import com.mgdsw.sdss.dto.challenge.SimpleChallengesResponse;
import com.mgdsw.sdss.dto.strategic.dashboard.DashboardFutureStrategicCommonResponse;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.enums.ChallengeStatus;
import com.mgdsw.sdss.enums.SurveyStatus;
import com.mgdsw.sdss.enums.SystemRoles;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface ChallengesService extends BaseService<Integer, Challenges> {

    ChallengesResponse create(ChallengesCreationRequest request, List<MultipartFile> files);

    ChallengesResponse update(Integer challengeId, ChallengesCreationRequest request, List<MultipartFile> files);

    PaginationResponse<ChallengesResponse> filter(ChallengeSearchRequest searchRequest, Pageable pageable);

    ChallengeAttendeesResponse retrieveAllUsersInvitedToThisChallengeId(Integer challengeId);

    ChallengesResponse viewChallengeScreen(Integer challengeId);

    ChallengeDashboardResponse retrieveChallengeDashboardResponse(Integer challengeId);

    void updateChallengeRoundLevel(Challenges challenges, Integer roundLevel, Integer isFinal);

    ChallengesResponse updateChallengeSurveyStatus(Integer challengeId, SurveyStatus surveyStatus);

    List<ChallengeAdminDashboardResponse> retrieveAllChallengesBasedOnStatus(ChallengeStatus status);

    List<ChallengeAdminDashboardResponse> retrieveAllChallengesBasedOnStatusForCurrentUser(ChallengeStatus status);

    List<ChallengeAdminDashboardResponse> retrieveAllChallenges();

    List<ChallengeAdminDashboardResponse> retrieveAllChallengesForCurrentUser();

    long countAllChallengesBasedOnStatus(ChallengeStatus status);

    long countAllChallengesBasedOnStatusForCurrentUser(ChallengeStatus status);

    long countAllChallenges();

    long countAllChallengesForCurrentUser();

    SimpleChallengesResponse updateChallengeStatus(Integer challengeId, ChallengeStatus status);

    PaginationResponse<ChallengesResponse> filterBasedOnInvitedUsers(ChallengeSearchRequest searchRequest, Pageable pageable);

    List<ChallengeForUserDashboardResponse> prepareTheChallengesForTheCurrentUserDashboardBasedOnStatusResponse(List<Challenges> challenesList,
                                                                                                                ChallengeStatus status, SystemRoles systemRoles);

    List<ChallengeAdminDashboardResponse> prepareTheChallengesForTheCurrentValidatorUserDashboardBasedOnStatusResponse(List<Challenges> challenesList,
                                                                                                                       ChallengeStatus status);

    String delete(Integer challengeId);

    String welcomeStartVotingEmail(Integer challengeId);

    String validatorStartValidateNewChallengeEmail(Integer challengeId);

    Long countByDepartmentId(String id);

    List<DashboardFutureStrategicCommonResponse> retrieveDashboardFutureStrategicGoalsResponse();

    List<DashboardFutureStrategicCommonResponse> retrieveDashboardFutureStrategicDirectionsResponse();

    byte[] generateChallengeReportPDF(Integer challengeId);
}
