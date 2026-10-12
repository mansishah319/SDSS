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
import com.mgdsw.sdss.dto.expertises.ExpertiseFilterRequest;
import com.mgdsw.sdss.dto.expertises.ExpertisesResponse;
import com.mgdsw.sdss.entity.variety.Expertises;
import com.mgdsw.sdss.repository.variety.ExpertisesRepository;
import com.mgdsw.sdss.service.ExpertisesService;
import com.mgdsw.sdss.specification.ExpertiseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class ExpertisesServiceImpl extends AbstractBaseServiceImpl<Integer, Expertises> implements ExpertisesService {

    private final ExpertisesRepository expertisesRepository;

    @Override
    protected ExpertisesRepository repository() {
        return expertisesRepository;
    }

    @Override
    public PaginationResponse<ExpertisesResponse> filter(ExpertiseFilterRequest search, Pageable pageable) {
        return super.filter(new ExpertiseSpecification(search), pageable, ExpertisesResponse.class);
    }
}