/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.service.impl;

import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.core.dto.lookup.DropDownListLookup;
import com.mgdsw.sdss.core.entity.Jobs;
import com.mgdsw.sdss.core.repository.JobsRepository;
import com.mgdsw.sdss.core.service.JobsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Service
@RequiredArgsConstructor
public class JobsServiceImpl implements JobsService {

    private final JobsRepository jobsRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional(readOnly = true)
    public DropDownListLookup findResponseById(Integer jobId) {
        return modelMapper.map(retrieveById(jobId), DropDownListLookup.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Jobs findById(Integer jobId) {
        return retrieveById(jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DropDownListLookup> retrieveAll() {
        return jobsRepository.findAll().stream().map(job ->
                modelMapper.map(job, DropDownListLookup.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<DropDownListLookup> retrieveAll(Pageable pageable, String search) {
        Page<Jobs> page = jobsRepository.searchByIdOrName(search, pageable);
        List<DropDownListLookup> dropDownListLookups = page.getContent().stream().map(job ->
                modelMapper.map(job, DropDownListLookup.class)).collect(Collectors.toList());
        return PaginationResponse.initPaginationResponse(page, pageable, dropDownListLookups);
    }

    private Jobs retrieveById(Integer jobId) {
        return jobsRepository.findById(jobId)
                .orElseThrow(() -> new CustomNotFoundException(
                        String.format(LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), jobId),
                        LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode()));
    }
}
