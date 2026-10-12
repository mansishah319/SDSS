/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.WeblisherDB.WEBLISHER_SCHEMA_NAME;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DP_EMP_MV_GRP", schema = WEBLISHER_SCHEMA_NAME)
public class Employee implements Serializable {

    @Id
    @Column(name = "GRP_SERL_NO")
    private Long id;

    @Column(name = "SLM_MILITARY_F")
    private Integer slmMilitaryFlag;

    @Column(name = "SLM_MILITARY_F_NAME")
    private String militaryFirstName;

    @Column(name = "SLM_EMP_NO")
    private String slmEmpNo;

    @Column(name = "PERSON_ID", nullable = false)
    private Long personId;

    @Column(name = "A$ASSIGNMENT_NUMBER")
    private String arabAssignmentNumber;

    @Column(name = "SLM_NAME")
    private String slmName;

    @Column(name = "A$LAST_NAME", nullable = false)
    private String arabLastName;

    @Column(name = "ADDRESS_LINE1")
    private String addressLine1;

    @Column(name = "ADDRESS_LINE2")
    private String addressLine2;

    @Column(name = "ADDRESS_LINE3")
    private String addressLine3;

    @Column(name = "ADDRESS_STYLE")
    private String addressStyle;

    @Column(name = "ADDRESS_TYPE")
    private String addressType;

    @Column(name = "AGE")
    private Long age;

    @Column(name = "ASSIGNMENT_NUMBER")
    private String assignmentNumber;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDateTime dateOfBirth;

    @Column(name = "SLM_AGE")
    private Long slmAge;

    @Column(name = "DATE_OF_HIRE")
    private LocalDateTime dateOfHire;

    @Column(name = "DATE_OF_LAST_HIRE")
    private LocalDateTime dateOfLastHire;

    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @Column(name = "EMPLOYEE_NUMBER")
    private String employeeNumber;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "PD_RELIGION")
    private Long pdReligion;

    @Column(name = "HOME_TELEPHONE")
    private String homeTelephone;

    @Column(name = "JOB_ID")
    private Long jobId;

    @Column(name = "JOB_NAME")
    private String jobName;

    @Column(name = "MANAGER_FLAG")
    private String managerFlag;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "NATIONALITY_CODE")
    private String nationalityCode;

    @Column(name = "NATIONALITY")
    private String nationality;

    @Column(name = "COUNTRY_OF_BIRTH_CODE")
    private String countryOfBirthCode;

    @Column(name = "COUNTRY_OF_BIRTH_DESC")
    private String countryOfBirthDesc;

    @Column(name = "OFFICE_NUMBER")
    private String officeNumber;

    @Column(name = "ORGANIZATION")
    private String departmentName;

    @Column(name = "ORGANIZATION_ID")
    private Long departmentId;

    @Column(name = "PERSON_START_DATE", nullable = false)
    private LocalDateTime personStartDate;

    @Column(name = "ORIGINAL_DATE_OF_HIRE")
    private LocalDateTime originalDateOfHire;

    @Column(name = "PERSONAL_TITLE")
    private String personalTitle;

    @Column(name = "POSITION_ID")
    private Long positionId;

    @Column(name = "POSITION_NAME")
    private String positionName;

    @Column(name = "GRADE_ID")
    private Long gradeId;

    @Column(name = "GRADE_NAME")
    private String gradeName;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "PREVIOUS_LAST_NAME")
    private String previousLastName;

    @Column(name = "PRINTABLE_ADDRESS_LINE1")
    private String printableAddressLine1;

    @Column(name = "PRINTABLE_ADDRESS_LINE2")
    private String printableAddressLine2;

    @Column(name = "PRINTABLE_ADDRESS_LINE3")
    private String printableAddressLine3;

    @Column(name = "PRINTABLE_ADDRESS_LINE4")
    private String printableAddressLine4;

    @Column(name = "PRINTABLE_ADDRESS_LINE5")
    private String printableAddressLine5;

    @Column(name = "PRINTABLE_ADDRESS_LINE6")
    private String printableAddressLine6;

    @Column(name = "REGION_1")
    private String region1;

    @Column(name = "REGION_3")
    private String region3;

    @Column(name = "SUPERVISOR_EMPLOYEE_NUMBER")
    private String supervisorId;

    @Column(name = "SUPERVISOR_FULL_NAME")
    private String supervisorFullName;

    @Column(name = "TELEPHONE_NUMBER_1")
    private String telephoneNumber1;

    @Column(name = "TELEPHONE_NUMBER_2")
    private String telephoneNumber2;

    @Column(name = "MARITAL_STATUS")
    private String maritalStatus;

    @Column(name = "QUALIFICATION_ID")
    private Long qualificationId;

    @Column(name = "PARENT_ORGANIZATION")
    private String parentDepartmentName;

    @Column(name = "PARENT_ORGANIZATION_ID")
    private Long parentDepartmentId;

    @Column(name = "PARENT_ORGANIZATION_TYPE")
    private String parentOrganizationType;

    @Column(name = "SLM_NAME_E")
    private String slmNameE;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "SECTION_NAME")
    private String sectionName;

    @Column(name = "SECTION_TYPE")
    private String sectionType;

    @Column(name = "SECTION_ID")
    private Long sectionId;

    @Column(name = "ASSIGNMENT_STATUS_ID", nullable = false)
    private Long assignmentStatusId;

    @Column(name = "ASSIGNMENT_STATUS")
    private String assignmentStatus;

    @Column(name = "ADJUSTED_SERVICE_DATE")
    private LocalDateTime adjustedServiceDate;

    @Column(name = "QUALIFICATION_TYPE")
    private String qualificationType;

    @Column(name = "PERSON_TITLE_AR")
    private String personTitleAr;

    @Column(name = "MARITAL_STATUS_DESC")
    private String maritalStatusDesc;

    @Column(name = "QUALIFICATION_SPECIFICATION")
    private String qualificationSpecification;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getId(), employee.getId()) &&
                Objects.equals(getPersonId(), employee.getPersonId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPersonId());
    }
}