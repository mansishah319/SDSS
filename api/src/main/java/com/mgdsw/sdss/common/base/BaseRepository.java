/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.base;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<I>, I extends Serializable> extends JpaRepository<T, I>, JpaSpecificationExecutor<T> {
    List<T> findAllByIdIn(List<I> idList);

    boolean existsById(@NonNull Integer id);

    long count();
}
