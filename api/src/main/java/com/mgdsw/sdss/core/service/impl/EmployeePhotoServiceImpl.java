/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.service.impl;

import com.mgdsw.sdss.core.repository.EmployeePhotoRepository;
import com.mgdsw.sdss.core.service.EmployeePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Service
@RequiredArgsConstructor
public class EmployeePhotoServiceImpl implements EmployeePhotoService {

    private final EmployeePhotoRepository employeePhotoRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] retrieveEmployeeImageByGrpNumber(Long grpNumber) {
        return employeePhotoRepository.getEmployeeImageByGrpNumber(grpNumber);
    }
}
