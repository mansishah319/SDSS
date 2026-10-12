/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.service.impl;

import com.mgdsw.sdss.core.entity.StfEmployeeInfo;
import com.mgdsw.sdss.core.repository.StfEmployeeInfoRepository;
import com.mgdsw.sdss.core.service.StfEmployeeInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 9月, 2024
 */

@Service
@RequiredArgsConstructor
public class StfEmployeeInfoServiceImpl implements StfEmployeeInfoService {

    private final StfEmployeeInfoRepository stfEmployeeInfoRepository;

    @Override
    @Transactional(readOnly = true)
    public StfEmployeeInfo findByGrpNumberOrNull(Long grpNumber) {
        return stfEmployeeInfoRepository.findByEmpGrpNum(grpNumber).orElse(null);
    }
}
