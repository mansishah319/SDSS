/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.lookup.user.dashboard;

import com.mgdsw.sdss.dto.lookup.user.TheUserDashboardResponse;
import com.mgdsw.sdss.dto.lookup.user.TheValidatorsDashboardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyMainDashboardResponse {

    private TheUserDashboardResponse userDashboardResponse;
    private TheValidatorsDashboardResponse validatorsDashboardResponse;
}
