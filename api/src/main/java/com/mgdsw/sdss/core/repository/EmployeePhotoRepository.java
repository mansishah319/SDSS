/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.repository;

import com.mgdsw.sdss.core.entity.EmployeePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Repository
public interface EmployeePhotoRepository extends JpaRepository<EmployeePhoto, Long> {

    @Query(value = "select emp.image from EmployeePhoto emp where emp.grpNumber= :grpNumber")
    byte[] getEmployeeImageByGrpNumber(@Param("grpNumber") Long grpNumber);
}
