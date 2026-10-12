/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.security;

import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * File security validator to detect phishing and malicious content in uploaded files
 *
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jan, 2025
 */
@Slf4j
public class FileSecurityValidator {

    // Suspicious file extensions that are commonly used for phishing
    private static final List<String> SUSPICIOUS_EXTENSIONS = Arrays.asList(
            "exe", "bat", "cmd", "com", "scr", "pif", "vbs", "js", "jar", "app", "deb", "pkg", "dmg",
            "msi", "run", "sh", "ps1", "psm1", "psd1", "ps1xml", "psc1", "cdxml", "ps1xml", "clixml",
            "html", "htm", "mhtml", "mht", "php", "asp", "aspx", "jsp", "pl", "py", "rb", "sh"
    );

    // Dangerous file types that could contain malicious content
    private static final List<String> DANGEROUS_MIME_TYPES = Arrays.asList(
            "application/x-msdownload", "application/x-msdos-program", "application/x-executable",
            "application/x-msdownload", "application/octet-stream", "text/html", "text/htm",
            "application/x-httpd-php", "application/x-asp", "application/x-jsp"
    );

    // Patterns that indicate phishing content. Keep each expression bounded/simple to avoid catastrophic backtracking.
    private static final List<Pattern> PHISHING_PATTERNS = Arrays.asList(
            Pattern.compile("https?://[^\\s]{1,2048}\\.(tk|ml|ga|cf|click|download|free|online|secure|verify|update|confirm)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("https?://bit\\.ly/", Pattern.CASE_INSENSITIVE),
            Pattern.compile("https?://tinyurl\\.com/", Pattern.CASE_INSENSITIVE),
            Pattern.compile("https?://goo\\.gl/", Pattern.CASE_INSENSITIVE),
            Pattern.compile("https?://t\\.co/", Pattern.CASE_INSENSITIVE),
            Pattern.compile("https?://ow\\.ly/", Pattern.CASE_INSENSITIVE),
            Pattern.compile("https?://is\\.gd/", Pattern.CASE_INSENSITIVE),
            Pattern.compile("https?://[^\\s]{1,2048}\\.(bank|paypal|amazon|apple|google|microsoft|facebook|twitter)[^\\s]{0,2048}", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script[^>]{0,512}>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bon(load|click|error|mouseover|focus|blur|change)\\s*=", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<iframe[^>]{0,512}>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(embed\\s+src|object\\s+data)\\s*=", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(window\\.location|document\\.location|location\\.href|location\\.replace)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("action\\s*=\\s*[\"']https?://[^\"']{1,2048}[\"']", Pattern.CASE_INSENSITIVE),
            Pattern.compile("method\\s*=\\s*[\"']post[\"']", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(mailto:|email\\s+address|contact\\s+information|personal\\s+details)\\b", Pattern.CASE_INSENSITIVE)
    );

    private static final List<String> DANGEROUS_TOKENS = Arrays.asList(
            "javascript:", "vbscript:", "data:text/html", "data:application/javascript"
    );

    private static final List<String> PHISHING_ACTION_WORDS = Arrays.asList(
            "verify", "confirm", "update", "activate", "reactivate", "validate", "secure", "urgent",
            "immediate", "expire", "expired", "suspended", "locked", "compromised", "breach", "hack",
            "password", "account", "login", "signin", "credentials"
    );

    private static final List<String> PHISHING_CLICK_WORDS = Arrays.asList(
            "click", "here", "now", "immediately", "urgently"
    );

    // Maximum file size for content scanning (10MB)
    private static final long MAX_SCAN_SIZE = 10L * 1024 * 1024; // Fixed: Added 'L' suffix for long literal

    /**
     * Validates a file for security threats including phishing content
     *
     * @param file The multipart file to validate
     * @throws CustomDataException if the file is deemed unsafe
     */
    public void validateFileSecurity(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return; // Skip validation for empty files
        }

        log.info("Starting security validation for file: {}", file.getOriginalFilename());

        // Check file extension
        validateFileExtension(file);

        // Check MIME type
        validateMimeType(file);

        // Scan file content for phishing patterns
        scanFileContent(file);

        log.info("Security validation passed for file: {}", file.getOriginalFilename());
    }

    /**
     * Validates file extension against suspicious extensions
     */
    private void validateFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();

        // Fixed: Merged if statements
        if (filename == null || SUSPICIOUS_EXTENSIONS.contains(FilenameUtils.getExtension(filename).toLowerCase())) {
            log.warn("Suspicious file extension detected for file: {}", filename);
            throw new CustomDataException(ErrorCodes.MALICIOUS_FILE_DETECTED.getMessage(),
                    ErrorCodes.MALICIOUS_FILE_DETECTED.getErrorCode());
        }
    }

    /**
     * Validates MIME type against dangerous types
     */
    private void validateMimeType(MultipartFile file) {
        String contentType = file.getContentType();

        // Fixed: Simplified condition by removing unnecessary null check separation
        if (contentType != null && DANGEROUS_MIME_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Dangerous MIME type detected: {} for file: {}", contentType, file.getOriginalFilename());
            throw new CustomDataException(ErrorCodes.MALICIOUS_FILE_DETECTED.getMessage(),
                    ErrorCodes.MALICIOUS_FILE_DETECTED.getErrorCode());
        }
    }

    /**
     * Scans file content for phishing patterns
     */
    private void scanFileContent(MultipartFile file) {
        try {
            // Skip content scanning for very large files
            if (file.getSize() > MAX_SCAN_SIZE) {
                log.warn("File too large for content scanning, skipping: {} (size: {} bytes)",
                        file.getOriginalFilename(), file.getSize());
                return;
            }

            byte[] content = file.getBytes();
            String contentString = extractTextContent(content, file.getOriginalFilename());

            // Fixed: Combined if statements
            if (contentString != null && !contentString.trim().isEmpty() && containsPhishingContent(contentString)) {
                log.warn("Phishing content detected in file: {}", file.getOriginalFilename());
                throw new CustomDataException(ErrorCodes.PHISHING_CONTENT_DETECTED.getMessage(),
                        ErrorCodes.PHISHING_CONTENT_DETECTED.getErrorCode());
            }

        } catch (IOException e) {
            log.error("Error reading file content for security scan: {}", e.getMessage());
            throw new CustomDataException(ErrorCodes.FILE_ERROR.getMessage(),
                    ErrorCodes.FILE_ERROR.getErrorCode());
        }
    }

    private boolean containsPhishingContent(String content) {
        String lowerContent = content.toLowerCase();
        for (String token : DANGEROUS_TOKENS) {
            if (lowerContent.contains(token)) {
                return true;
            }
        }
        for (Pattern pattern : PHISHING_PATTERNS) {
            if (pattern.matcher(content).find()) {
                return true;
            }
        }
        return containsActionAndClickWords(lowerContent);
    }

    private boolean containsActionAndClickWords(String lowerContent) {
        boolean hasActionWord = PHISHING_ACTION_WORDS.stream().anyMatch(lowerContent::contains);
        boolean hasClickWord = PHISHING_CLICK_WORDS.stream().anyMatch(lowerContent::contains);
        return hasActionWord && hasClickWord;
    }

    private String extractPdfTextObjects(String contentStr) {
        StringBuilder text = new StringBuilder();
        int searchFrom = 0;
        int start;
        int end;

        while ((start = contentStr.indexOf("BT", searchFrom)) >= 0
                && (end = contentStr.indexOf("ET", start + 2)) >= 0) {

            int contentStart = start + 2;

            if (end - contentStart <= 4096) {
                text.append(contentStr, contentStart, end).append(' ');
            }

            searchFrom = end + 2;
        }

        return text.toString();
    }

    /**
     * Extracts text content from file bytes based on the file type
     */
    private String extractTextContent(byte[] content, String filename) {
        try {
            String extension = FilenameUtils.getExtension(filename).toLowerCase();

            switch (extension) {
                case "txt":
                case "csv":
                case "json":
                case "xml":
                case "html":
                case "htm":
                case "js":
                case "css":
                    return new String(content, StandardCharsets.UTF_8);

                case "pdf":
                    // For PDF files, extract text content (basic implementation)
                    return extractTextFromPdf(content);

                case "doc":
                case "docx":
                    // For Word documents, extract text content (basic implementation)
                    return extractTextFromWord(content);

                default:
                    // For other file types, try to extract readable text
                    return extractReadableText(content);
            }
        } catch (Exception e) {
            log.warn("Could not extract text content from file: {}, error: {}", filename, e.getMessage());
            return null;
        }
    }

    /**
     * Basic PDF text extraction (simplified implementation)
     */
    private String extractTextFromPdf(byte[] content) {
        try {
            // This is a simplified implementation. In production, you might want to use
            // a proper PDF library like Apache PDFBox
            String contentStr = new String(content, StandardCharsets.UTF_8);

            return extractPdfTextObjects(contentStr);
        } catch (Exception e) {
            log.warn("Error extracting text from PDF: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Basic Word document text extraction (simplified implementation)
     */
    private String extractTextFromWord(byte[] content) {
        try {
            // This is a simplified implementation. In production, you might want to use
            // Apache POI for proper Word document parsing
            String contentStr = new String(content, StandardCharsets.UTF_8);

            // Look for readable text patterns
            Pattern textPattern = Pattern.compile("[a-zA-Z]{3,}");
            java.util.regex.Matcher matcher = textPattern.matcher(contentStr);

            StringBuilder text = new StringBuilder();
            while (matcher.find()) {
                text.append(matcher.group()).append(" ");
            }

            return text.toString();
        } catch (Exception e) {
            log.warn("Error extracting text from Word document: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract readable text from binary content
     */
    private String extractReadableText(byte[] content) {
        try {
            String contentStr = new String(content, StandardCharsets.UTF_8);

            // Filter out non-printable characters and extract readable text
            StringBuilder readableText = new StringBuilder();
            for (char c : contentStr.toCharArray()) {
                if (Character.isLetterOrDigit(c) || Character.isWhitespace(c) ||
                        c == '.' || c == ',' || c == ':' || c == ';' || c == '!' ||
                        c == '?' || c == '@' || c == '#' || c == '$' || c == '%' ||
                        c == '&' || c == '*' || c == '(' || c == ')' || c == '-' ||
                        c == '_' || c == '+' || c == '=' || c == '/' || c == '\\' ||
                        c == '<' || c == '>' || c == '"' || c == '\'') {
                    readableText.append(c);
                }
            }

            return readableText.toString();
        } catch (Exception e) {
            log.warn("Error extracting readable text: {}", e.getMessage());
            return null;
        }
    }
}
