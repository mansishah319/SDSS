/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.security;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Secure error handler to prevent information disclosure in error messages
 *
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jan, 2025
 */
@Slf4j
public class SecureErrorHandler {

    // Common error message constant to avoid duplication
    private static final String GENERIC_ERROR_MESSAGE = "An error occurred. Please try again.";

    // Patterns that might expose sensitive information
    private static final List<String> SENSITIVE_PATTERNS = Arrays.asList(
            // SQL patterns
            "SELECT\\s+.*\\s+FROM", "INSERT\\s+INTO", "UPDATE\\s+.*\\s+SET", "DELETE\\s+FROM",
            "DROP\\s+TABLE", "CREATE\\s+TABLE", "ALTER\\s+TABLE",

            // File system patterns
            "/etc/passwd", "/etc/shadow", "C:\\\\Windows", "/home/", "/var/",

            // Script patterns
            "<script", "javascript:", "vbscript:", "onload=", "onclick=",

            // Database connection patterns
            "jdbc:", "mysql:", "postgresql:", "oracle:",

            // Stack trace patterns
            "Exception in thread", "at java\\.", "at sun\\.", "at com\\.sun\\.",

            // Internal system paths
            "com\\.mgdsw", "org\\.springframework", "org\\.hibernate"
    );

    // Compiled pattern for efficient matching
    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
            String.join("|", SENSITIVE_PATTERNS),
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    /**
     * Sanitizes an error message to prevent information disclosure
     *
     * @param originalMessage The original error message
     * @return Sanitized error message safe for user display
     */
    public String sanitizeErrorMessage(String originalMessage) {
        if (originalMessage == null || originalMessage.trim().isEmpty()) {
            return GENERIC_ERROR_MESSAGE;
        }

        // Check if the message contains sensitive information
        if (SENSITIVE_PATTERN.matcher(originalMessage).find()) {
            logWarnMessageWithTruncation(originalMessage);
            return GENERIC_ERROR_MESSAGE;
        }

        // Check for common exception patterns that might expose internal details
        if (containsInternalExceptionDetails(originalMessage)) {
            log.warn("Internal exception details detected, sanitizing error message");
            return GENERIC_ERROR_MESSAGE;
        }

        // If a message is too long, truncate it
        if (originalMessage.length() > 200) {
            return originalMessage.substring(0, 200) + "...";
        }

        return originalMessage;
    }

    /**
     * Checks if the message contains internal exception details
     */
    private boolean containsInternalExceptionDetails(String message) {
        String lowerMessage = message.toLowerCase();

        // Check for Java exception class names
        if (lowerMessage.contains("exception") &&
                (lowerMessage.contains("java.lang") ||
                        lowerMessage.contains("org.springframework") ||
                        lowerMessage.contains("org.hibernate") ||
                        lowerMessage.contains("com.mgdsw"))) {
            return true;
        }

        // Check for stack trace indicators
        if (lowerMessage.contains("at ") && lowerMessage.contains("(") && lowerMessage.contains(")")) {
            return true;
        }

        // Check for internal method names
        return lowerMessage.contains("method") && lowerMessage.contains("class");
    }

    /**
     * Logs the original error securely without exposing sensitive information
     *
     * @param originalMessage The original error message
     * @param exception       The exception that occurred
     */
    public void logErrorSecurely(String originalMessage, Exception exception) {
        // Log the sanitized message for user context
        log.error("Error occurred: {}", sanitizeErrorMessage(originalMessage));

        // Log the full exception details for debugging (this goes to server logs only)
        if (exception != null) {
            log.debug("Full exception details: ", exception);
        }

        // Log the original message in debug mode only (not in production)
        if (log.isDebugEnabled()) {
            log.debug("Original error message: {}", originalMessage);
        }
    }

    /**
     * Determines if an error message is safe to return to the user
     *
     * @param message The error message to check
     * @return true if safe, false if contains sensitive information
     */
    public boolean isErrorMessageSafe(String message) {
        if (message == null || message.trim().isEmpty()) {
            return true;
        }

        return !SENSITIVE_PATTERN.matcher(message).find() &&
                !containsInternalExceptionDetails(message);
    }

    /**
     * Helper method to log warning messages with truncation to avoid code duplication
     */
    private void logWarnMessageWithTruncation(String originalMessage) {
        String truncatedMessage = originalMessage.length() > 100 ?
                originalMessage.substring(0, 100) + "..." : originalMessage;
        log.warn("{}{}", "Sensitive information detected in error message, sanitizing: ", truncatedMessage);
    }
}
