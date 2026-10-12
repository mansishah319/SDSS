/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.roles.CreateRoleRequest;
import com.mgdsw.sdss.dto.roles.RolesResponse;
import com.mgdsw.sdss.entity.user.Roles;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface RolesService extends BaseService<Integer, Roles> {

    RolesResponse createNewRole(CreateRoleRequest request);

    List<Roles> retrieveAllUserRoles(List<Integer> roleIds);

    List<RolesResponse> findAllActiveRoles();
}
