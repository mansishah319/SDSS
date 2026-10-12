/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeGrpResponse {

    // Employee details
    private Long id;
    private Integer slmMilitaryFlag;
    private String militaryFirstName;
    private String slmEmpNo;
    private Long personId;
    private String arabAssignmentNumber;
    private String slmName;
    private String arabLastName;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressStyle;
    private String addressType;
    private Long age;
    private String assignmentNumber;
    private String dateOfBirth;
    private Long slmAge;
    private String dateOfHire;
    private String dateOfLastHire;
    private String emailAddress;
    private String employeeNumber;
    private String fullName;
    private String gender;
    private Long pdReligion;
    private String homeTelephone;
    private Long jobId;
    private String jobName;
    private String managerFlag;
    private String middleName;
    private String nationalityCode;
    private String nationality;
    private String countryOfBirthCode;
    private String countryOfBirthDesc;
    private String officeNumber;
    private String departmentName;
    private Long departmentId;
    private String personStartDate;
    private String originalDateOfHire;
    private String personalTitle;
    private Long positionId;
    private String positionName;
    private Long gradeId;
    private String gradeName;
    private String postalCode;
    private String previousLastName;
    private String printableAddressLine1;
    private String printableAddressLine2;
    private String printableAddressLine3;
    private String printableAddressLine4;
    private String printableAddressLine5;
    private String printableAddressLine6;
    private String region1;
    private String region3;
    private String supervisorId;
    private String supervisorFullName;
    private String telephoneNumber1;
    private String telephoneNumber2;
    private String maritalStatus;
    private Long qualificationId;
    private String parentDepartmentName;
    private Long parentDepartmentId;
    private String parentOrganizationType;
    private String slmNameE;
    private String firstName;
    private String lastName;
    private String sectionName;
    private String sectionType;
    private Long sectionId;
    private Long assignmentStatusId;
    private String assignmentStatus;
    private String adjustedServiceDate;
    private String qualificationType;
    private String personTitleAr;
    private String maritalStatusDesc;
    private String qualificationSpecification;

    private String empMobile;
    private String shortName;

    @JsonProperty("shortDepartmentName")
    public String getShortDepartmentName() {
        return shortName;
    }
}
