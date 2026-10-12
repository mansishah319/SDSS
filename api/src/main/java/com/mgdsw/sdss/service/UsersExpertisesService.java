/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.user.RetrieveUsersBasedOnExpertisesRequest;
import com.mgdsw.sdss.dto.user.expertises.UsersExpertisesRequest;
import com.mgdsw.sdss.dto.user.expertises.UsersExpertisesResponse;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.UsersExpertises;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.Operations;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */
public interface UsersExpertisesService extends BaseService<Integer, UsersExpertises> {

    void createListOfExpertisesByUser(List<UsersExpertisesRequest> usersExpertisesRequestList, User user, Operations operations);

    List<UsersExpertisesResponse> retrieveUsersExpertisesBasedOnUser(User user, Language userLanguage);

    UsersExpertises findMainUsersExpertises(User user);

    List<User> retrieveUsersBasedOnAreaOfExpertiseAndLevel(List<RetrieveUsersBasedOnExpertisesRequest> usersExpertisesRequestList);
}
