/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.AlphaNum;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.DASH;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.DOT;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.DOUBLE_SLASH;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.EMPTY_STRING;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.QUESTION_MARK;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.STAR;
import static com.mgdsw.sdss.common.constants.DpConstants.Regex.ARABIC_REGEX;

@Slf4j
public class AlphaNumValidator implements ConstraintValidator<AlphaNum, String> {

    private static final String[] SPECIAL_CHARACTERS = {DOT, String.valueOf(DASH), DOUBLE_SLASH, STAR, QUESTION_MARK};

    private Pattern pattern = null;
    private String message;
    private AlphaNum alphaNum;

    @Override
    public void initialize(AlphaNum textOnly) {
        this.alphaNum = textOnly;
        this.message = textOnly.message();

        pattern = Pattern.compile(String.format(ARABIC_REGEX, sanitizeSpecialCharacters(alphaNum.allowedChars())));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean result = pattern.matcher(value).matches();

        if (!result) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message.replace("{allowed}", alphaNum.allowedChars()))
                    .addConstraintViolation();
        }

        return result;
    }

    private String sanitizeSpecialCharacters(String allowedChars) {
        return Stream.of(SPECIAL_CHARACTERS)
                .map(specialChar -> specialChar.replace(DOUBLE_SLASH, "\\\\"))
                .collect(Collectors.joining(EMPTY_STRING, allowedChars, EMPTY_STRING));
    }
}
