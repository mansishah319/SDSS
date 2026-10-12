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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.WeblisherDB.WEBLISHER_SCHEMA_NAME;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 9月, 2024
 */

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STF_EMPLOYEES", schema = WEBLISHER_SCHEMA_NAME)
public class StfEmployeeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_seq")
    @SequenceGenerator(name = "employee_id_seq", sequenceName = "WEBLISHER.STF_EMPLOYEES_SEQ", allocationSize = 1)
    @Column(name = "EMPLOYEE_ID", nullable = false)
    private Long employeeId;

    @Column(name = "EMP_MILITARY_F", nullable = false)
    private Integer empMilitaryF = 1;

    @Column(name = "EMP_NO", nullable = false)
    private Long empNo;

    @Column(name = "USR_LOGIN_NAME", length = 30)
    private String usrLoginName;

    @Column(name = "EMP_EMAIL", length = 100)
    private String empEmail;

    @Column(name = "EMP_FAX", length = 20)
    private String empFax;

    @Column(name = "EMP_MOBILE", length = 200)
    private String empMobile;

    @Column(name = "EMP_PHONE_RES", length = 20)
    private String empPhoneRes;

    @Column(name = "EMP_PHONE_RES2", length = 20)
    private String empPhoneRes2;

    @Column(name = "EMP_PHONE_OFF", length = 20)
    private String empPhoneOff;

    @Column(name = "EMP_ADRS_RES", length = 100)
    private String empAdrsRes;

    @Column(name = "EMP_ADRS_OFF", length = 100)
    private String empAdrsOff;

    @Column(name = "EMP_ONDUTY_F", nullable = false)
    private Integer empOndutyF = 1;

    @Column(name = "EMP_REPLACE_ID")
    private Long empReplaceId;

    @Column(name = "EMP_DEPT")
    private Long empDept;

    @Column(name = "EMP_PHOTO_ID")
    private Long empPhotoId;

    @Column(name = "EMP_PASSPORT_IN_F", nullable = false)
    private Integer empPassportInF = 1;

    @Column(name = "EMP_PASSPORT_INOUT_DATE")
    private LocalDate empPassportInOutDate;

    @Column(name = "EMP_PASSPORT_OUT_REASON", length = 240)
    private String empPassportOutReason;

    @Column(name = "EMP_VISA_COPY_ID")
    private Long empVisaCopyId;

    @Column(name = "EMP_PASSPORT_COPY_ID")
    private Long empPassportCopyId;

    @Column(name = "EMP_GEN_DEPT")
    private Long empGenDept;

    @Column(name = "EMP_GRP_NUM")
    private Long empGrpNum;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;
}
