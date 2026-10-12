/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.user;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.user.Roles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface RolesRepository extends BaseRepository<Roles, Integer> {

    @Query("SELECT r FROM Roles r WHERE r.id IN :ids")
    List<Roles> findAllByActiveTrueAndIdIn(List<Integer> ids);

    List<Roles> findAllByActiveIsTrue();
}
