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
@Table(schema = WEBLISHER_SCHEMA_NAME, name = "DP_HR_DEPARTMENTS_OPR")
public class ExtDepartment implements Serializable {

    @Id
    @Column(name = "SUB_ORGANIZATION_ID")
    private Long id;

    @Column(name = "DEP_ORDER")
    private Long depOrder;

    @Column(name = "PARENT_ORGANIZATION_ID")
    private Long parentDepartmentId;

    @Column(name = "PARENT_ORGANIZATION_TYPE_CODE")
    private Long parentDepartmentTypeCode;

    @Column(name = "SUB_ORGANIZATION_TYPE")
    private String departmentType;

    @Column(name = "SUB_ORGANIZATION_TYPE_CODE")
    private Long departmentTypeCode;

    @Column(name = "SUBORDINATE_ORGANIZATION")
    private String departmentName;

    @Column(name = "IS_EXT")
    private Boolean extended;
    @Column(name = "IS_ACTIVE")
    private Boolean active;
    @Column(name = "OPERATION_NUM")
    private Long operationNumber;
    @Column(name = "IS_DD")
    private Boolean mainDepartment;
}
