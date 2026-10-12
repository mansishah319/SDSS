/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.surveys;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Repository
public interface SdssSurveyUsersRepository extends BaseRepository<SdssSurveyUsers, Integer> {

    Optional<SdssSurveyUsers> findByEmployeeGrpNumber(Long employeeGrpNumber);

    Optional<SdssSurveyUsers> findByEmployeeInfoKey(String employeeInfoKey);

    List<SdssSurveyUsers> findByEmployeeGrpNumberIn(List<Long> employeeGrpNumberList);
}
