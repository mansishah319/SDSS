/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.user;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.UsersExpertises;
import com.mgdsw.sdss.enums.ExpertLevel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Repository
public interface UsersExpertisesRepository extends BaseRepository<UsersExpertises, Integer> {

    List<UsersExpertises> findAllByUser(User user);

    UsersExpertises findByUserAndIsPrimaryTrue(User user);

    void deleteByUser(User user);

    @Query("SELECT ue.user FROM UsersExpertises ue WHERE ue.expertises.id IN :expertisesList AND ue.expertLevel IN :expertLevelList AND ue.user.status = 1")
    List<User> findUserByExpertisesInAndExpertLevelIn(@Param("expertisesList") List<Integer> expertisesList,
                                                      @Param("expertLevelList") List<ExpertLevel> expertLevelList);
}
