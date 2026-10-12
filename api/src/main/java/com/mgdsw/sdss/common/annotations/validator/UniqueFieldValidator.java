/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.UniqueField;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */
public class UniqueFieldValidator implements ConstraintValidator<UniqueField, Object> {

    private String fieldName;

    @Override
    public void initialize(UniqueField constraintAnnotation) {
        fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(Object list, ConstraintValidatorContext context) {
        try {
            Set<Object> fieldValues = new HashSet<>();
            for (Object item : (Iterable<?>) list) {
                Method getter = findGetterMethod(item.getClass(), fieldName);
                Object value = getter.invoke(item);
                if (!fieldValues.add(value)) {
                    return false; // Duplicate found
                }
            }
            return true; // No duplicates
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new CustomDataException(e.getMessage(), ErrorCodes.UNIQUENESS_ERROR.getErrorCode());
        }
    }

    private Method findGetterMethod(Class<?> clazz, String fieldName) throws NoSuchMethodException {
        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            return clazz.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            // If no standard getter is found, try "is" prefix for boolean fields
            getterName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            return clazz.getMethod(getterName);
        }
    }
}