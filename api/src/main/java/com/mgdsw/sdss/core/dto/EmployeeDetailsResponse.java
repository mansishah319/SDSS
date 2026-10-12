/**
 * ***************************************************************
 * Copyright ©1956-2026 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailsResponse {

    private Long id;
    private Long personId;
    private String employeeNumber;
    private String fullName;
    private String slmName;
    private Long departmentId;
    private String departmentName;
    private Long jobId;
    private String jobName;
    private String telephoneNumber1;
    private String emailAddress;
}
