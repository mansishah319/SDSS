/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.repository;

import com.mgdsw.sdss.core.entity.StfEmployeeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 9月, 2024
 */

@Repository
public interface StfEmployeeInfoRepository extends JpaRepository<StfEmployeeInfo, Long> {

    Optional<StfEmployeeInfo> findByEmpGrpNum(Long empGrpNum);
}
