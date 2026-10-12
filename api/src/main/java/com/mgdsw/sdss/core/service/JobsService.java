/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.service;

import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.core.dto.lookup.DropDownListLookup;
import com.mgdsw.sdss.core.entity.Jobs;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */
public interface JobsService {

    DropDownListLookup findResponseById(Integer jobId);

    Jobs findById(Integer jobId);

    List<DropDownListLookup> retrieveAll();

    PaginationResponse<DropDownListLookup> retrieveAll(Pageable pageable, String search);
}
