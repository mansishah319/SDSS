/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeInfoKeyResponse implements Serializable {

    // Employee details
    private Long id;
    private Long personId;

    private String emailAddress;
    private String gender;
    private String fullName;
    private String arabLastName;

    private Long jobId;
    private String jobName;

    private String officeNumber;

    private String departmentName;
    private Long departmentId;

    private String personalTitle;
    private Long positionId;
    private String positionName;

    private Long gradeId;
    private String gradeName;

    private String parentDepartmentName;

    private Long parentDepartmentId;
    private String parentOrganizationType;

    private String slmNameE;
    private String firstName;
    private String lastName;
    private String sectionName;
    private String sectionType;
    private Long sectionId;

    private String personTitleAr;
    private String maritalStatusDesc;

    private String qualificationType;
    private String qualificationSpecification;
}
