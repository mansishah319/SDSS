/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.ValidFutureDateTimes;
import com.mgdsw.sdss.common.utils.DpDateUtil;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jul, 2024
 */
public class FutureDateTimesValidator implements ConstraintValidator<ValidFutureDateTimes, List<String>> {

    private ZoneId zoneId;

    @Value("${app.timeZone}")
    private String timeZone;

    @Override
    public void initialize(ValidFutureDateTimes constraintAnnotation) {
        this.zoneId = ZoneId.of(timeZone);
    }

    @Override
    public boolean isValid(List<String> dateTimes, ConstraintValidatorContext context) {
        if (dateTimes == null) {
            return true;
        }
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        return DpDateUtil.convertStringsToZonedDateTimes(dateTimes, timeZone).stream()
                .allMatch(zonedDateTime -> {
                    try {
                        return !zonedDateTime.isBefore(now);
                    } catch (Exception e) {
                        return false;
                    }
                });
    }
}
