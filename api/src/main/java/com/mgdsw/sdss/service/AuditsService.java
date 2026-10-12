/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.dto.audits.AuditsResponse;
import com.mgdsw.sdss.dto.audits.AuditsSearchRequest;
import com.mgdsw.sdss.dto.audits.CreateAuditsRequest;
import com.mgdsw.sdss.entity.variety.Audits;
import org.springframework.data.domain.Pageable;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface AuditsService extends BaseService<Integer, Audits> {

    AuditsResponse createNewAudits(CreateAuditsRequest createAuditsRequest);

    AuditsResponse findAuditsByID(Integer auditId);

    AuditsResponse updateAudits(CreateAuditsRequest createAuditsRequest, Integer auditId);

    PaginationResponse<AuditsResponse> findAllAudits(Pageable pageable);

    PaginationResponse<AuditsResponse> auditsFiltration(AuditsSearchRequest search, Pageable pageable);
}
