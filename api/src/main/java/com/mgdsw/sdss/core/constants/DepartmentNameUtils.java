/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.core.constants;

import java.util.Arrays;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

public final class DepartmentNameUtils {

    /**
     * Processes department name according to type code rules
     *
     * @param departmentName           Full department name
     * @param departmentTypeCode       Department type code
     * @param parentDepartmentTypeCode Parent department type code (nullable)
     * @param departmentId             Department ID (nullable)
     * @param shortName                Pre-computed short name (nullable)
     * @return Processed short name
     */
    public static String processDepartmentName(
            String departmentName,
            Integer departmentTypeCode,
            Integer parentDepartmentTypeCode,
            Long departmentId,
            String shortName) {

        // Return pre-computed short name if available
        if (shortName != null) {
            return shortName;
        }

        // Null check for required fields
        if (departmentName == null || departmentTypeCode == null) {
            return null;
        }

        try {
            return determineShortName(
                    departmentName,
                    departmentTypeCode,
                    parentDepartmentTypeCode,
                    departmentId
            );
        } catch (Exception ex) {
            return departmentName; // Fallback to original name
        }
    }

    private static String determineShortName(
            String departmentName,
            Integer departmentTypeCode,
            Integer parentDepartmentTypeCode,
            Long departmentId) {

        // Handle null departmentTypeCode (shouldn't happen due to earlier check, but defensive programming)
        if (departmentTypeCode == null) {
            return cleanDepartmentName(departmentName);
        }

        // Type-specific processing rules
        switch (departmentTypeCode) {
            case 1:
                return extractBeforeFirstDash(departmentName);

            case 22:
                if (!departmentName.startsWith("مكتب")) {
                    return extractBeforeFirstDash(departmentName);
                }
                break;

            case 40:
                if (departmentId != null && departmentId.equals(55702L)) {
                    return extractBeforeFirstDash(departmentName);
                }
                return extractBetweenDashes(departmentName);

            case 60:
                return extractBetweenDashes(departmentName);

            case 3:
                if (parentDepartmentTypeCode != null && parentDepartmentTypeCode == 120) {
                    return extractBetweenDashes(departmentName);
                }
                return extractAfterLastDash(departmentName);

            default:
                // For any unhandled type codes, apply default cleaning
                return cleanDepartmentName(departmentName);
        }

        // If we fell through the switch -> (only possible for case 22 with مكتب prefix)
        return cleanDepartmentName(departmentName);
    }

    // Extraction methods ------------------------------------------------------

    public static String extractBeforeFirstDash(String name) {
        int index = name.indexOf("-");
        return (index > 0) ? name.substring(0, index).trim() : name;
    }

    public static String extractBetweenDashes(String name) {
        int firstIndex = name.indexOf("-");
        int lastIndex = name.lastIndexOf("-");
        if (firstIndex >= 0 && lastIndex > firstIndex) {
            return name.substring(firstIndex + 1, lastIndex).trim();
        }
        return name;
    }

    public static String extractAfterLastDash(String name) {
        int lastIndex = name.lastIndexOf("-");
        return (lastIndex >= 0) ? name.substring(lastIndex + 1).trim() : name;
    }

    public static String cleanDepartmentName(String name) {
        return cleanDepartmentName(name, DEFAULT_SUFFIXES);
    }

    // Optional: Method with custom suffixes
    public static String cleanDepartmentName(String name, List<String> customSuffixes) {
        String cleaned = name;
        for (String suffix : customSuffixes) {
            cleaned = cleaned.replace(suffix, "");
        }
        return cleaned.trim();
    }

    // Common suffixes to remove (can be customized)
    private static final List<String> DEFAULT_SUFFIXES = Arrays.asList(
            "- مدير المخفر", "- مدير إدارة فرعية",
            "- مدير ادارة فرعية", "- مدير مركز",
            "- رئيس قسم", "- مدير الادارة", "- مدير المركز"
    );

    private DepartmentNameUtils() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
