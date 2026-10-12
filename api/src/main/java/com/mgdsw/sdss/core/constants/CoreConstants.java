/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.constants;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
public final class CoreConstants {

    public static class EmployeeGrp {

        public static final String HR_EMPLOYEE_BASE_URL = "/hr/employee";
        public static final String EMPLOYEE_BASE_URL = "/api/v1/employee";
        public static final String DEPARTMENT_BASE_URL = "/hr/department";
        public static final String FIND_EMPLOYEE_BY_GRP_URL = EMPLOYEE_BASE_URL + "/{grpNumber}";
        public static final String EMPLOYEE_GRP_FILTRATION_URL = EMPLOYEE_BASE_URL + "/search/grp";
        public static final String RETRIEVE_EMPLOYEE_WITH_DEPARTMENT_INFO_BY_GRP_NUMBER_URL = EMPLOYEE_BASE_URL + "/info/{grpNumber}";
        public static final String GRP_NUMBER = "grpNumber";
        public static final String GET_ALL_DEPARTMENTS = DEPARTMENT_BASE_URL + "/mainDepartmentAndPoliceStation";
        public static final String GET_FULL_DEPARTMENTS = DEPARTMENT_BASE_URL + "/mainSelect";
        public static final String GET_ALL_SUB_DEPARTMENTS = DEPARTMENT_BASE_URL + "/subDepartmentList/{departmentId}";
        public static final String RETRIEVE_ALL_DROPDOWN_LIST_OF_EMPLOYEES = HR_EMPLOYEE_BASE_URL + "/dropdown-list";

        private EmployeeGrp() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    private CoreConstants() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
