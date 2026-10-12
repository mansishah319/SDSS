/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.utils;

import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.helper.DateTimeComparator;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.constants.DpConstants.DateFormat.DUBAI_ZONE_ID;
import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */
public final class DpDateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_SLASH_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIME_T_FORMAT = "yyyy-MM-dd'T'HH:mm";
    public static final String DATE_TIME_FORMAT = DATE_FORMAT + SPACE + TIME_FORMAT;
    private static final ZoneId ZONE_ID = ZoneId.of(DUBAI_ZONE_ID);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final DateTimeFormatter TIME_T_FORMATTER = DateTimeFormatter.ofPattern(TIME_T_FORMAT);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter DATE_SLASH_FORMATTER = DateTimeFormatter.ofPattern(DATE_SLASH_FORMAT);

    private DpDateUtil() {
        throw new IllegalStateException(UTILITY_CLASS);
    }

    public static Long convertDateToLong(Date date) {
        if (date != null) {
            return date.getTime() / 1000; // Convert milliseconds to seconds
        }
        return null;
    }

    public static LocalDateTime toLocalDateTime(String date) {
        return parseDateTime(date, DATE_TIME_FORMATTER);
    }

    public static String toStringDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public static LocalDate toLocalDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    public static LocalDate toSlashLocalDate(String date) {
        return LocalDate.parse(date, DATE_SLASH_FORMATTER);
    }

    public static String startTimeFromLocalDate(String date) {
        return toSlashLocalDate(date).atStartOfDay(ZONE_ID).toLocalDateTime().format(DATE_TIME_FORMATTER);
    }

    public static String endTimeFromLocalDate(String date) {
        return toSlashLocalDate(date)
                .atStartOfDay(ZONE_ID)
                .plusDays(1) // Add one day to reach the next day
                .minusNanos(1) // Subtract 1 nanosecond to get the last moment of the previous day
                .format(DATE_TIME_FORMATTER);
    }

    public static LocalDate parseFormattedLocalDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    public static LocalDateTime parseFormattedLocalDateTime(String dateString) {
        return LocalDateTime.parse(dateString, DATE_TIME_FORMATTER);
    }

    public static String localDateTimeToTime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT));
    }

    public static String localDateTimeToDate(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_FORMATTER);
    }

    public static List<LocalDateTime> convertStringsToLocalDateTimes(List<String> dateStrings) {
        return dateStrings.stream()
                .map(date -> parseDateTime(date, DATE_TIME_FORMATTER))
                .collect(Collectors.toList());
    }

    public static List<ZonedDateTime> convertStringsToZonedDateTimes(List<String> dateStrings, String timeZone) {
        return dateStrings.stream()
                .map(date -> parseDateTime(date, DATE_TIME_FORMATTER).atZone(ZoneId.of(timeZone)))
                .collect(Collectors.toList());
    }

    public static String parseDateTimeToFormatedString(String dateTimeString, DateTimeFormatter formatter) {
        try {
            LocalDateTime localDateTime = DateTimeHandlerUtil.parseToLocalDateTime(dateTimeString);
            return localDateTime.format(formatter);
        } catch (DateTimeParseException e) {
            // Handle parsing exception or log it if needed
            throw new CustomDataException("It's Invalid date-time format: " + dateTimeString, e.getMessage());
        }
    }

    public static LocalDateTime parseDateTime(String dateTimeString, DateTimeFormatter formatter) {
        try {
            LocalDateTime localDateTime = DateTimeHandlerUtil.parseToLocalDateTime(dateTimeString);
            String formattedDate = localDateTime.format(formatter);
            return LocalDateTime.parse(formattedDate, formatter);
        } catch (DateTimeParseException e) {
            // Handle parsing exception or log it if needed
            throw new CustomDataException("Invalid date-time format: " + dateTimeString, e.getMessage());
        }
    }

    // New method to convert string like "2024-10-17T10:18" to LocalDateTime
    public static LocalDateTime tStringConvertToLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }

        // Parse the input string with its original pattern
        LocalDateTime localDateTime = parseDateTime(dateTimeString, TIME_T_FORMATTER);

        // Format the LocalDateTime to "yyyy-MM-dd HH:mm:ss"
        return LocalDateTime.parse(localDateTime.format(DATE_TIME_FORMATTER), DATE_TIME_FORMATTER);
    }

    // Utility method to check if a start time is before an end time
    public static boolean isStartDateTimeBeforeEndDateTime(String startTime, String endTime, String dateTimePattern) {
        return compareDateTimes(startTime, endTime, dateTimePattern, LocalDateTime::isBefore);
    }

    // Utility method to check if a start time is after the current date and time
    public static boolean isDateTimeAfterCurrentDateTime(String startTime, String dateTimePattern) {
        return compareWithCurrentDateTime(startTime, dateTimePattern, LocalDateTime::isAfter);
    }

    public static boolean isDateTimeAfterCurrentDateTimeT(String startTime) {
        return isDateTimeAfterCurrentDateTime(startTime, TIME_T_FORMAT);
    }

    public static boolean isStartDateTimeBeforeEndDateTimeT(String startTime, String endTime) {
        return isStartDateTimeBeforeEndDateTime(startTime, endTime, TIME_T_FORMAT);
    }

    private static boolean compareDateTimes(String startTime, String endTime, String pattern,
                                            DateTimeComparator comparator) {
        if (startTime == null || endTime == null) {
            return false;
        }
        LocalDateTime start = parseDateTime(startTime, DateTimeFormatter.ofPattern(pattern));
        LocalDateTime end = parseDateTime(endTime, DateTimeFormatter.ofPattern(pattern));
        return comparator.compare(start, end);
    }

    private static boolean compareWithCurrentDateTime(String startTime, String pattern,
                                                      DateTimeComparator comparator) {
        if (startTime == null) {
            return false;
        }
        LocalDateTime start = parseDateTime(startTime, DateTimeFormatter.ofPattern(pattern));
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        return comparator.compare(start, now);
    }

    /**
     * Validates the reminder time. Throws an exception if the reminder time is before the current time.
     *
     * @param zonedDateTime the reminder time to be validated
     * @param zoneId        the time zone to be used
     */
    public static void validateThatTimeAfterNow(ZonedDateTime zonedDateTime, ZoneId zoneId) {
        if (zonedDateTime.isBefore(ZonedDateTime.now(zoneId))) {
            throw new CustomDataException(
                    String.format(
                            LocalizedErrorCodes.REMINDER_TIME_IS_WRONG.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.REMINDER_TIME_IS_WRONG.getErrorCode()
            );
        }
    }
}

