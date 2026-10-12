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
import com.mgdsw.sdss.dto.user.CreateAdminUserRequest;
import com.mgdsw.sdss.dto.user.RetrieveUsersBasedOnExpertisesRequest;
import com.mgdsw.sdss.dto.user.UpdateMyProfileRequest;
import com.mgdsw.sdss.dto.user.UserDashboardResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import com.mgdsw.sdss.dto.user.UserDropdownSearch;
import com.mgdsw.sdss.dto.user.UserResponse;
import com.mgdsw.sdss.dto.user.UserRolesResponse;
import com.mgdsw.sdss.dto.user.UserSearchRequest;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.Status;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface UserService extends BaseService<Integer, User> {

    boolean isUserExistInSdssSystem(Long grpNumber);

    boolean isUserExistInSdssSystem();

    UserResponse addAdminUser(CreateAdminUserRequest createGuestUserRequest);

    User findUserByGrpNumber(Long grpNumber);

    UserRolesResponse getUserAuthorities();

    PaginationResponse<UserResponse> userFiltration(UserSearchRequest search, Pageable pageable);

    UserResponse changeUserLanguage(String language);

    Language retrieveUserLanguage();

    ByteArrayInputStream exportUsersExcelSheet(UserSearchRequest search, Pageable pageable);

    PaginationResponse<UserResponse> getAllUsers(Pageable pageable);

    List<UserResponse> getAllUsers();

    String deleteUserByGrp(Long grpNumber);

    User findUserByGrpNumberAndStatus(Long grpNumber, Status status);

    String confirmUserAccount(Long grpNumber, String confirmationToken);

    UserResponse retrieveUserByGrpNumber(Long grpNumber);

    UserResponse retrieveUserByGrpNumber();

    UserResponse updateAdminUser(CreateAdminUserRequest createAdminUserRequest);

    List<UserDropDownListResponse> findValidatorUsers();

    List<UserDropDownListResponse> findModeratorUsers();

    List<UserDropDownListResponse> findActiveUserMembers();

    List<UserDropDownListResponse> retrieveUsersBasedOnAreaOfExpertiseAndLevel(RetrieveUsersBasedOnExpertisesRequest userInvitationExpertisesRequest);

    List<User> findByGrpNumberInAndStatusAndRolesNameEn(List<Long> grpNumberList, String roleName);

    User retrieveCurrentUser();

    User findUserByEmail(String email);

    List<UserDropDownListResponse> getAllUsersWithPrimaryExpertisesSorted(String expertLevel, String areaOfExpertise);

    UserResponse updateAdminUserStatus(Long grpNumber, String userStatus);

    List<UserDashboardResponse> retrieveUsersByStatus(Status status);

    long countAllByStatus(Status status);

    long countAll();

    List<UserDashboardResponse> retrieveInternalUsers();

    List<UserDashboardResponse> retrieveExternalUsers();

    long countAllInternalUsers();

    long countAllExternalUsers();

    void updateUserOnlineStatus(Long grpNumber, Boolean onlineStatus);

    Long retrieveGrpNumber();

    List<User> findAllByGrpNumberIn(List<Long> grpNumberList);

    List<Long> retrieveAllSdssUsersGrpNumbers();

    User findUserByGrpNumberAndStatus(Long grpNumber, Status status, Language language);

    PaginationResponse<UserDropDownListResponse> filterAllUsersWithPrimaryExpertisesSorted(UserDropdownSearch search, Pageable pageable);

    UserResponse updateMyProfile(UpdateMyProfileRequest request);
}
