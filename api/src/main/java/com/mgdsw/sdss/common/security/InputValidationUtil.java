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
 * Input validation utility to prevent XSS and injection attacks
 *
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jan, 2025
 */
@Slf4j
public class InputValidationUtil {

    private static final String JAVASCRIPT_COLUMN_STR = "javascript:";

    // Security patterns. Keep each expression small/bounded to avoid regex denial-of-service.
    private static final List<Pattern> XSS_PATTERNS = Arrays.asList(
            Pattern.compile("<script[^>]{0,512}>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bon(load|click|error|mouseover|focus|blur|change|submit|reset|select)\\s*=", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<(iframe|embed|object|applet)[^>]{0,512}>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(expression|url)\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bunion\\s+select\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(drop\\s+table|delete\\s+from|insert\\s+into|alter\\s+table|create\\s+table|truncate\\s+table)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bupdate\\s++[^;]{0,200}+\\s++set\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\\|\\s*(rm|del)|;\\s*(rm|del)|&&\\s*(rm|del))", Pattern.CASE_INSENSITIVE),
            Pattern.compile("`[^`]{0,512}`"),
            Pattern.compile("\\$\\([^)]{0,512}\\)"),
            Pattern.compile("\\$\\{[^}]{0,512}\\}")
    );

    private static final List<String> DANGEROUS_TOKENS = Arrays.asList(
            JAVASCRIPT_COLUMN_STR, "vbscript:", "data:text/html", "data:application/javascript"
    );

    private static final Pattern SCRIPT_BLOCK_PATTERN = Pattern.compile("<script[^>]{0,512}>[\\s\\S]{0,20000}?</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern IFRAME_BLOCK_PATTERN = Pattern.compile("<iframe[^>]{0,512}>[\\s\\S]{0,20000}?</iframe>", Pattern.CASE_INSENSITIVE);
    private static final Pattern OBJECT_BLOCK_PATTERN = Pattern.compile("<object[^>]{0,512}>[\\s\\S]{0,20000}?</object>", Pattern.CASE_INSENSITIVE);
    private static final Pattern APPLET_BLOCK_PATTERN = Pattern.compile("<applet[^>]{0,512}>[\\s\\S]{0,20000}?</applet>", Pattern.CASE_INSENSITIVE);
    private static final Pattern DANGEROUS_TAG_PATTERN = Pattern.compile("<(embed|object|applet)[^>]{0,512}>", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_HANDLER_PATTERN = Pattern.compile("\\s*on[a-z]{2,30}\\s*=\\s*[\"'][^\"']{0,2000}[\"']", Pattern.CASE_INSENSITIVE);
    private static final Pattern CSS_EXPRESSION_PATTERN = Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE);

    /**
     * Validates input for XSS and injection attacks
     *
     * @param input The input string to validate
     * @return true if input is safe, false if malicious content detected
     */
    public boolean isValidInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true;
        }

        if (containsDangerousInput(input)) {
            log.warn("XSS pattern detected in input: {}",
                    input.length() > 100 ? input.substring(0, 100) + "..." : input);
            return false;
        }

        return true;
    }

    private boolean containsDangerousInput(String input) {
        String lowerInput = input.toLowerCase();
        for (String token : DANGEROUS_TOKENS) {
            if (lowerInput.contains(token)) {
                return true;
            }
        }
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sanitizes input by removing potentially dangerous content
     *
     * @param input The input string to sanitize
     * @return Sanitized input string
     */
    public String sanitizeInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String sanitized = SCRIPT_BLOCK_PATTERN.matcher(input).replaceAll("");
        sanitized = IFRAME_BLOCK_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = OBJECT_BLOCK_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = APPLET_BLOCK_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = DANGEROUS_TAG_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = EVENT_HANDLER_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = sanitized.replace(JAVASCRIPT_COLUMN_STR, "").replace(JAVASCRIPT_COLUMN_STR, "");
        sanitized = sanitized.replace("vbscript:", "").replace("VBScript:", "");
        sanitized = CSS_EXPRESSION_PATTERN.matcher(sanitized).replaceAll("");

        return sanitized.trim();
    }

    /**
     * Escapes HTML entities to prevent XSS
     *
     * @param input The input string to escape
     * @return HTML-escaped string
     */
    public String escapeHtml(String input) {
        if (input == null) {
            return null;
        }

        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * Validates and sanitizes input for safe storage
     *
     * @param input The input string to validate and sanitize
     * @return Sanitized input if valid, null if malicious
     */
    public String validateAndSanitize(String input) {
        if (!isValidInput(input)) {
            return null;
        }

        return sanitizeInput(input);
    }

    /**
     * Checks if input contains SQL injection patterns
     *
     * @param input The input string to check
     * @return true if contains SQL injection patterns
     */
    public boolean containsSqlInjection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        String lowerInput = input.toLowerCase();
        return lowerInput.contains("union select") ||
                lowerInput.contains("drop table") ||
                lowerInput.contains("delete from") ||
                lowerInput.contains("insert into") ||
                lowerInput.contains("update ") ||
                lowerInput.contains("alter table") ||
                lowerInput.contains("create table") ||
                lowerInput.contains("truncate table");
    }

    /**
     * Validates file name for security
     *
     * @param fileName The file name to validate
     * @return true if file name is safe
     */
    public boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        // Check for path traversal
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return false;
        }

        // Check for XSS patterns
        return isValidInput(fileName);
    }

    /**
     * Validates JSON input for security
     *
     * @param jsonInput The JSON string to validate
     * @return true if JSON is safe
     */
    public boolean isValidJsonInput(String jsonInput) {
        if (jsonInput == null || jsonInput.trim().isEmpty()) {
            return true;
        }

        // Check for script injection in JSON
        if (jsonInput.toLowerCase().contains("script") ||
                jsonInput.toLowerCase().contains(JAVASCRIPT_COLUMN_STR)) {
            return false;
        }

        return isValidInput(jsonInput);
    }
}
