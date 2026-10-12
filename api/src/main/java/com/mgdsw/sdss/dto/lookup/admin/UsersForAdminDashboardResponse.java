/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.lookup.admin;

import com.mgdsw.sdss.dto.user.UserDashboardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn May 2024
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersForAdminDashboardResponse {

    private long activeUsersCount;
    private long deletedUsersCount;
    private long notActiveUsersCount;

    private long internalUsersCount;
    private long externalUsersCount;

    private long totalUsersCount;

    private List<UserDashboardResponse> activeUsersList;
    private List<UserDashboardResponse> deletedUsersList;
    private List<UserDashboardResponse> notActiveUsersList;

    private List<UserDashboardResponse> internalAdminUsersList;
    private List<UserDashboardResponse> externalGuestUsersList;
}
