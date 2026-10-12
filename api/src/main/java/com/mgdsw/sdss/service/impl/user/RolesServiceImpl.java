/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.user;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.roles.CreateRoleRequest;
import com.mgdsw.sdss.dto.roles.RolesResponse;
import com.mgdsw.sdss.entity.user.Roles;
import com.mgdsw.sdss.repository.user.RolesRepository;
import com.mgdsw.sdss.service.RolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.UNDER_SCORE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class RolesServiceImpl extends AbstractBaseServiceImpl<Integer, Roles> implements RolesService {

    private final RolesRepository rolesRepository;

    @Override
    protected RolesRepository repository() {
        return rolesRepository;
    }

    @Override
    public RolesResponse createNewRole(CreateRoleRequest request) {
        request.setNameEn(request.getNameEn().toUpperCase().replace(SPACE, UNDER_SCORE));
        return super.create(request, RolesResponse.class);
    }

    @Override
    public List<Roles> retrieveAllUserRoles(List<Integer> roleIds) {
        return rolesRepository.findAllByActiveTrueAndIdIn(roleIds);
    }

    @Override
    public List<RolesResponse> findAllActiveRoles() {
        return super.getAll(RolesResponse.class);
    }
}