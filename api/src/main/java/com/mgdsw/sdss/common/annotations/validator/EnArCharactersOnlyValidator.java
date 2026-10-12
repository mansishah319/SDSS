package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.EnArCharactersOnly;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * -----------------------------------------------------------------------------
 * Project: sdss
 * Author: engmoabdu
 * Created On: 08/10/2025 at 6:07 AM
 * Email: eng.mo.abdu@gmail.com
 *
 * @version: vr0.1
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Dubai Police
 * All rights reserved.
 * -----------------------------------------------------------------------------
 */
public class EnArCharactersOnlyValidator implements ConstraintValidator<EnArCharactersOnly, String> {

    // ✅ English letters, Arabic letters, Arabic-Indic digits, and European digits
    private static final String EN_AR_NUMERIC_PATTERN =
            "^[\\p{IsArabic}\\p{M}\\p{IsLatin}\\p{Nd}\\s.,!?؛،؟:-]*$";

    // ✅ Compile once for performance
    private static final Pattern VALIDATION_PATTERN = Pattern.compile(EN_AR_NUMERIC_PATTERN);

    // Forbidden characters
    private static final String[] FORBIDDEN_CHARS = {
            "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "=",
            "{", "}", "[", "]", "|", ":", ";", "\"", "'", "<", ">", "/", "~", "`", "\\"
    };

    @Override
    public void initialize(EnArCharactersOnly constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.trim().isEmpty()) return false;

        // ✅ Check the allowed pattern
        if (!VALIDATION_PATTERN.matcher(value).matches()) {
            return false;
        }

        // ✅ Check forbidden characters
        for (String forbidden : FORBIDDEN_CHARS) {
            if (value.contains(forbidden)) {
                return false;
            }
        }

        // ✅ Ensure there's at least one Arabic, Latin letter, or digit
        return Pattern.compile(".*[\\p{IsArabic}\\p{IsLatin}\\p{Nd}].*").matcher(value).matches();
    }
}
