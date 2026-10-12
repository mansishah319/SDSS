package com.mgdsw.sdss.core.projection;

/**
 * -----------------------------------------------------------------------------
 * Project: core-system
 * Author: engmoabdu
 * Created On: 27/01/2026 at 2:54 PM
 * Email: eng.mo.abdu@gmail.com
 *
 * @version: vr0.1
 * -----------------------------------------------------------------------------
 * Copyright (c) 2026 Dubai Police
 * All rights reserved.
 * -----------------------------------------------------------------------------
 */
public interface DepartmentHierarchyProjection {

    Long getSectorId();

    String getSectorName();

    Long getSectorTypeCode();

    Long getParentDepartmentId();
}
