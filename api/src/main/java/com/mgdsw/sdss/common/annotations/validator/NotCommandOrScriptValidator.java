/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class NotCommandOrScriptValidator implements ConstraintValidator<NotCommandOrScript, String> {

    // Common dangerous patterns that might indicate commands or scripts
    private static final List<String> DANGEROUS_PATTERNS = Arrays.asList(
            // Shell commands
            "\\brm\\b", "\\bdel\\b", "\\bformat\\b", "\\bshutdown\\b", "\\breboot\\b", "\\bkill\\b", "\\bkillall\\b",
            "\\bsudo\\b", "\\bsu\\b", "\\bchmod\\b", "\\bchown\\b", "\\bpasswd\\b", "\\buseradd\\b", "\\buserdel\\b",
            "\\bmount\\b", "\\bumount\\b", "\\bfdisk\\b", "\\bmkfs\\b", "\\bdd\\b", "cat\\s+/", "grep\\s+-r",

            // Script execution patterns
            "eval\\s*\\(", "exec\\s*\\(", "system\\s*\\(", "shell_exec\\s*\\(",
            "passthru\\s*\\(", "popen\\s*\\(", "proc_open\\s*\\(",

            // SQL injection patterns
            "drop\\s+table", "delete\\s+from", "truncate\\s+table", "alter\\s+table",
            "create\\s+table", "insert\\s+into", "update\\s+.*set", "union\\s+select",

            // Script tags and execution
            "<script", "</script>", "javascript:", "vbscript:", "onload\\s*=",
            "onclick\\s*=", "onerror\\s*=", "onmouseover\\s*=",

            // Command injection patterns
            ";\\s*rm", ";\\s*del", "&&\\s*rm", "&&\\s*del", "\\|\\s*rm", "\\|\\s*del",
            "`[^`]*`", "\\$\\([^)]*\\)", "\\$\\{[^}]*\\}",

            // File system operations
            "\\.\\./", "\\.\\.[\\\\/]", "/etc/passwd", "/etc/shadow", "c:\\\\windows",
            "cmd\\.exe", "powershell\\.exe", "/bin/sh", "/bin/bash",

            // Network operations
            "\\bwget\\b", "\\bcurl\\b", "\\bnc\\b", "\\bnetcat\\b", "\\btelnet\\b", "\\bssh\\b", "\\bftp\\b",

            // Process and service operations
            "ps\\s+aux", "ps\\s+-ef", "\\bhtop\\b",
            "\\bservice\\s+\\S+\\s+(start|stop|restart|reload|enable|disable|status)\\b",
            "\\bsystemctl\\s+(start|stop|restart|reload|enable|disable|status)\\b"
    );

    private static final Pattern SUSPICIOUS_PATTERN = Pattern.compile(
            String.join("|", DANGEROUS_PATTERNS),
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    @Override
    public void initialize(NotCommandOrScript constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // null or empty values are considered valid
        }

        // Check for dangerous patterns
        boolean containsDangerousContent = SUSPICIOUS_PATTERN.matcher(value).find();

        if (containsDangerousContent) {
            log.warn("Potentially dangerous content detected in input: {}",
                    value.length() > 50 ? value.substring(0, 50) + "..." : value);

            // Use the default message from annotation (which is now internationalized)
            return false;
        }

        return true;
    }
}
