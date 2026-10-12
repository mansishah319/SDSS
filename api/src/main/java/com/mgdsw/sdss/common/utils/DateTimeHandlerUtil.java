/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.utils;

import com.mgdsw.sdss.common.helper.DateTimeComparator;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */
public final class DateTimeHandlerUtil {

    public static final ZoneId DUBAI_ZONE = ZoneId.of("Asia/Dubai");

    // Common date formats
    private static final String[] FORMATS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "MM/dd/yyyy'T'HH:mm:ss.SSSZ",
            "MM/dd/yyyy'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "MM/dd/yyyy'T'HH:mm:ssZ",
            "MM/dd/yyyy'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss",
            "yyyy:MM:dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm",
            "yyyyMMdd"
    };

    public static LocalDateTime parseToLocalDateTime(String dateStr) {
        return parseToLocalDateTimeOptional(dateStr).orElseThrow(() -> new CustomDataException("Date is wrong", "3334"));
    }

    /**
     * Parses a date string to LocalDateTime using predefined formats.
     *
     * @param dateStr the date string to parse
     * @return Optional of LocalDateTime if parsing is successful, otherwise empty
     */
    public static Optional<LocalDateTime> parseToLocalDateTimeOptional(String dateStr) {
        return Arrays.stream(FORMATS)
                .map(format -> tryParse(dateStr, format))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    // Helper method to attempt to parse with a given format
    private static Optional<LocalDateTime> tryParse(String dateStr, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return Optional.of(LocalDateTime.parse(dateStr, formatter));
        } catch (DateTimeParseException e) {
            // Fail silently and return empty optional
            return Optional.empty();
        }
    }

    /**
     * Converts a LocalDateTime to ZonedDateTime in Dubai timezone.
     *
     * @param localDateTime the LocalDateTime to convert
     * @return ZonedDateTime in Dubai timezone
     */
    public static ZonedDateTime toDubaiZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(DUBAI_ZONE);
    }

    /**
     * Compares the given LocalDateTime with the current time in Dubai timezone.
     *
     * @param dateTime the LocalDateTime to compare
     * @return 1 if it's in the future, -1 if it's in the past, 0 if it's the same time
     */
    public static int compareWithCurrentTime(LocalDateTime dateTime) {
        ZonedDateTime dubaiCurrentTime = ZonedDateTime.now(DUBAI_ZONE);
        ZonedDateTime dubaiDateTime = toDubaiZonedDateTime(dateTime);
        return dubaiDateTime.compareTo(dubaiCurrentTime);
    }

    /**
     * Compares two LocalDateTime objects based on Dubai timezone.
     *
     * @param firstDateTime  the first LocalDateTime
     * @param secondDateTime the second LocalDateTime
     * @return 1 if the firstDateTime is after the secondDateTime, -1 if it's before, 0 if they are equal
     */
    public static int compare(LocalDateTime firstDateTime, LocalDateTime secondDateTime) {
        ZonedDateTime firstDubaiTime = toDubaiZonedDateTime(firstDateTime);
        ZonedDateTime secondDubaiTime = toDubaiZonedDateTime(secondDateTime);
        return firstDubaiTime.compareTo(secondDubaiTime);
    }

    public static boolean compare(String startTime, DateTimeComparator comparator) {
        LocalDateTime start = parseToLocalDateTime(startTime);
        LocalDateTime now = LocalDateTime.now(DUBAI_ZONE);
        return comparator.compare(start, now);
    }

    public static boolean compare(String startTime, String endTime, DateTimeComparator comparator) {
        LocalDateTime start = parseToLocalDateTime(startTime);
        LocalDateTime end = parseToLocalDateTime(endTime);
        return comparator.compare(start, end);
    }

    public static int getCurrentYear() {
        return Year.now(ZoneId.of("Asia/Dubai")).getValue();
    }

    private DateTimeHandlerUtil() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
