/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.service;

import com.mgdsw.sdss.core.entity.StfEmployeeInfo;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 9月, 2024
 */
public interface StfEmployeeInfoService {

    StfEmployeeInfo findByGrpNumberOrNull(Long grpNumber);
}
