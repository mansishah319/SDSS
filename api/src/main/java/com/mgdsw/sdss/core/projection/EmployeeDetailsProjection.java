/**
 * ***************************************************************
 * Copyright ©1956-2026 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.core.projection;

/**
 * -----------------------------------------------------------------------------
 * Project: core-system
 * Author: engmoabdu
 * Created On: 09/07/2026 at 2:54 PM
 * Email: eng.mo.abdu@gmail.com
 *
 * @version: vr0.1
 * -----------------------------------------------------------------------------
 * Copyright (c) 2026 Dubai Police
 * All rights reserved.
 * -----------------------------------------------------------------------------
 */

public interface EmployeeDetailsProjection {

    Long getId();

    Long getPersonId();

    String getEmployeeNumber();

    String getFullName();

    String getSlmName();

    Long getDepartmentId();

    String getDepartmentName();

    Long getJobId();

    String getJobName();

    String getTelephoneNumber1();

    String getEmailAddress();
}
