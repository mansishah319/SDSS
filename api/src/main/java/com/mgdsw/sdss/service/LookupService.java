/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.dto.lookup.ChallengeLookupCreationScreenResponse;
import com.mgdsw.sdss.dto.lookup.UserListLookupScreenResponse;
import com.mgdsw.sdss.dto.lookup.admin.AdminDashboardResponse;
import com.mgdsw.sdss.dto.lookup.home.MainHomeScreenResponse;
import com.mgdsw.sdss.dto.lookup.home.MainHomeSurveyScreenResponse;
import com.mgdsw.sdss.dto.lookup.user.dashboard.MyMainDashboardResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */
public interface LookupService {

    UserListLookupScreenResponse getUserListScreenLookupData();

    ChallengeLookupCreationScreenResponse retrieveChallengeLookupCreationScreenResponse();

    List<UserDropDownListResponse> retrieveTeamMembersBasedOnExpertises(String expertLevel, String areaOfExpertise);

    AdminDashboardResponse retrieveAdminDashboardLookupData();

    MyMainDashboardResponse myMainDashboard();

    MainHomeScreenResponse retrieveHomePageStatistics();

    MainHomeSurveyScreenResponse retrieveSurveyHomePageStatistics();
}
