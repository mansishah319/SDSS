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
import com.mgdsw.sdss.enums.Status;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface UserRepository extends BaseRepository<User, Integer>, RevisionRepository<User, Integer, Integer> {

    Optional<User> findByGrpNumber(Long grpNumber);

    Optional<User> findByGrpNumberAndStatus(Long grpNumber, Status status);

    Optional<User> findByGrpNumberAndConfirmationToken(Long grpNumber, String confirmationToken);

    List<User> findByGrpNumberInAndStatusAndRolesNameEn(List<Long> grpNumbers, Status status, String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.nameEn = :roleName")
    List<User> findByRolesNameEn(String roleName);

    List<User> findAllByStatus(Status status);

    Optional<User> findUserByEmail(String email);

    long countByStatus(Status status);

    long count();

    long countByIsInternalTrue();

    long countByIsInternalFalse();

    List<User> findAllByIsInternalTrue();

    List<User> findByIsInternalFalse();

    List<User> findAllByGrpNumberIn(List<Long> grpNumberList);

    @Query("SELECT u.grpNumber FROM User u")
    List<Long> findAllGrpNumbers();
}
