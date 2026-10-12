/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.repository;

import com.mgdsw.sdss.core.entity.Jobs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Repository
public interface JobsRepository extends JpaRepository<Jobs, Integer> {

    @Query("SELECT j FROM Jobs j WHERE " +
            "(:search IS NULL OR " +
            "(:search IS NOT NULL AND CAST(j.id AS string) = :search) OR " +
            "(:search IS NOT NULL AND LOWER(j.name) LIKE LOWER(CONCAT('%', :search, '%'))))")
    Page<Jobs> searchByIdOrName(@Param("search") String search, Pageable pageable);
}
