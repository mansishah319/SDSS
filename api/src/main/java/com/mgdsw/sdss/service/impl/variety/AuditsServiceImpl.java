/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.variety;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.dto.audits.AuditsResponse;
import com.mgdsw.sdss.dto.audits.AuditsSearchRequest;
import com.mgdsw.sdss.dto.audits.CreateAuditsRequest;
import com.mgdsw.sdss.entity.variety.Audits;
import com.mgdsw.sdss.repository.variety.AuditsRepository;
import com.mgdsw.sdss.service.AuditsService;
import com.mgdsw.sdss.specification.AuditsSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditsServiceImpl extends AbstractBaseServiceImpl<Integer, Audits> implements AuditsService {

    private final AuditsRepository auditsRepository;

    @Override
    protected AuditsRepository repository() {
        return auditsRepository;
    }

    @Override
    @Transactional
    public AuditsResponse createNewAudits(CreateAuditsRequest createAuditsRequest) {
        return super.create(modelMapper.map(createAuditsRequest, Audits.class), AuditsResponse.class);
    }

    @Override
    public AuditsResponse findAuditsByID(Integer auditId) {
        return super.findById(auditId, AuditsResponse.class);
    }

    @Override
    public AuditsResponse updateAudits(CreateAuditsRequest createAuditsRequest, Integer auditId) {
        return super.update(modelMapper.map(createAuditsRequest, Audits.class), auditId, AuditsResponse.class);
    }

    @Override
    public PaginationResponse<AuditsResponse> findAllAudits(Pageable pageable) {
        return super.getAllWithPagination(pageable, AuditsResponse.class);
    }

    @Override
    public PaginationResponse<AuditsResponse> auditsFiltration(AuditsSearchRequest search, Pageable pageable) {
        return super.filter(new AuditsSpecification(search), pageable, AuditsResponse.class);
    }
}