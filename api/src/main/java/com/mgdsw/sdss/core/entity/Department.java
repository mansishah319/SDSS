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
import javax.persistence.Transient;
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
@Table(schema = WEBLISHER_SCHEMA_NAME, name = "DP_HR_DEPARTMENTS_GRP")
public class Department implements Serializable {

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

    @Transient
    private String shortName;

    @Transient
    public String getShortName() {
        if (shortName != null) {
            return shortName;
        }

        if (departmentName == null || departmentTypeCode == null) {
            return null;
        }

        try {
            shortName = determineShortName();
        } catch (Exception ex) {
            shortName = departmentName; // Fallback to original name in case of error
        }

        return shortName;
    }

    private String determineShortName() {
        if (departmentTypeCode == 1 || (departmentTypeCode == 22 && !departmentName.startsWith("مكتب"))) {
            return extractBeforeFirstDash(departmentName);
        }
        if (departmentTypeCode == 40) {
            return (id != null && id.equals(55702L))
                    ? extractBeforeFirstDash(departmentName)
                    : extractBetweenDashes(departmentName);
        }
        if (departmentTypeCode == 60) {
            return extractBetweenDashes(departmentName);
        }
        if (departmentTypeCode == 3) {
            return (parentDepartmentTypeCode != null && parentDepartmentTypeCode == 120)
                    ? extractBetweenDashes(departmentName)
                    : extractAfterLastDash(departmentName);
        }
        return cleanDepartmentName(departmentName);
    }

    private String extractBeforeFirstDash(String name) {
        int index = name.indexOf("-");
        return (index > 0) ? name.substring(0, index) : name;
    }

    private String extractBetweenDashes(String name) {
        int firstIndex = name.indexOf("-");
        int lastIndex = name.lastIndexOf("-");
        return (firstIndex >= 0 && lastIndex > firstIndex) ? name.substring(firstIndex + 2, lastIndex) : name;
    }

    private String extractAfterLastDash(String name) {
        int lastIndex = name.lastIndexOf("-");
        return (lastIndex >= 0) ? name.substring(lastIndex + 1) : name;
    }

    private String cleanDepartmentName(String name) {
        return name.replace("- مدير المخفر", "")
                .replace("- مدير إدارة فرعية", "")
                .replace("- مدير ادارة فرعية", "")
                .replace("- مدير مركز", "")
                .replace("- رئيس قسم", "")
                .replace("- مدير الادارة", "")
                .replace("- مدير المركز", "");
    }
}
