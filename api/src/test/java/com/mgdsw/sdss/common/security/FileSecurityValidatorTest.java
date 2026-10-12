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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for FileSecurityValidator
 *
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jan, 2025
 */
@ExtendWith(MockitoExtension.class)
class FileSecurityValidatorTest {

    private FileSecurityValidator fileSecurityValidator;

    @BeforeEach
    void setUp() {
        fileSecurityValidator = new FileSecurityValidator();
    }

    @Test
    void testValidFileUpload_ShouldPass() {
        // Test with a valid text file
        MultipartFile validFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "This is a valid file content".getBytes()
        );

        assertDoesNotThrow(() -> fileSecurityValidator.validateFileSecurity(validFile));
    }

    @Test
    void testSuspiciousFileExtension_ShouldThrowException() {
        // Test with an executable file
        MultipartFile suspiciousFile = new MockMultipartFile(
                "file", "malicious.exe", "application/x-msdownload", "malicious content".getBytes()
        );

        CustomDataException exception = assertThrows(CustomDataException.class,
                () -> fileSecurityValidator.validateFileSecurity(suspiciousFile));

        assertEquals(ErrorCodes.MALICIOUS_FILE_DETECTED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testDangerousMimeType_ShouldThrowException() {
        // Test with a dangerous MIME type
        MultipartFile dangerousFile = new MockMultipartFile(
                "file", "script.js", "application/x-executable", "alert('hack')".getBytes()
        );

        CustomDataException exception = assertThrows(CustomDataException.class,
                () -> fileSecurityValidator.validateFileSecurity(dangerousFile));

        assertEquals(ErrorCodes.MALICIOUS_FILE_DETECTED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testPhishingContentInTextFile_ShouldThrowException() {
        // Test with phishing content
        String phishingContent = "Click here to verify your account: https://bit.ly/fake-bank-login";
        MultipartFile phishingFile = new MockMultipartFile(
                "file", "phishing.txt", "text/plain", phishingContent.getBytes()
        );

        CustomDataException exception = assertThrows(CustomDataException.class,
                () -> fileSecurityValidator.validateFileSecurity(phishingFile));

        assertEquals(ErrorCodes.PHISHING_CONTENT_DETECTED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testJavaScriptInjection_ShouldThrowException() {
        // Test with JavaScript injection
        String maliciousContent = "<script>window.location='http://malicious-site.com'</script>";
        MultipartFile maliciousFile = new MockMultipartFile(
                "file", "malicious.html", "text/html", maliciousContent.getBytes()
        );

        CustomDataException exception = assertThrows(CustomDataException.class,
                () -> fileSecurityValidator.validateFileSecurity(maliciousFile));

        assertEquals(ErrorCodes.MALICIOUS_FILE_DETECTED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testSuspiciousUrls_ShouldThrowException() {
        // Test with suspicious URLs
        String suspiciousContent = "Visit our secure site: https://paypal-verification.tk/login";
        MultipartFile suspiciousFile = new MockMultipartFile(
                "file", "suspicious.txt", "text/plain", suspiciousContent.getBytes()
        );

        CustomDataException exception = assertThrows(CustomDataException.class,
                () -> fileSecurityValidator.validateFileSecurity(suspiciousFile));

        assertEquals(ErrorCodes.PHISHING_CONTENT_DETECTED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testValidPdfFile_ShouldPass() {
        // Test with valid PDF content (simplified)
        String validPdfContent = "This is a valid PDF document with legitimate content.";
        MultipartFile validFile = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", validPdfContent.getBytes()
        );

        assertDoesNotThrow(() -> fileSecurityValidator.validateFileSecurity(validFile));
    }

    @Test
    void testValidImageFile_ShouldPass() {
        // Test with a valid image file
        MultipartFile validFile = new MockMultipartFile(
                "file", "image.jpg", "image/jpeg", "fake image content".getBytes()
        );

        assertDoesNotThrow(() -> fileSecurityValidator.validateFileSecurity(validFile));
    }

    @Test
    void testEmptyFile_ShouldPass() {
        // Test with an empty file
        MultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.txt", "text/plain", new byte[0]
        );

        assertDoesNotThrow(() -> fileSecurityValidator.validateFileSecurity(emptyFile));
    }

    @Test
    void testNullFile_ShouldPass() {
        // Test with a null file
        assertDoesNotThrow(() -> fileSecurityValidator.validateFileSecurity(null));
    }

    @Test
    void testLargeFile_ShouldPass() {
        // Test with a large file (should skip content scanning)
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MultipartFile largeFile = new MockMultipartFile(
                "file", "large.txt", "text/plain", largeContent
        );

        assertDoesNotThrow(() -> fileSecurityValidator.validateFileSecurity(largeFile));
    }

    @Test
    void testSuspiciousBatchFile_ShouldThrowException() {
        // Test with a batch file
        MultipartFile batchFile = new MockMultipartFile(
                "file", "script.bat", "application/x-bat", "del /f /q C:\\Windows\\System32".getBytes()
        );

        CustomDataException exception = assertThrows(CustomDataException.class,
                () -> fileSecurityValidator.validateFileSecurity(batchFile));

        assertEquals(ErrorCodes.MALICIOUS_FILE_DETECTED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testPhishingKeywords_ShouldThrowException() {
        // Test with phishing keywords
        String phishingContent = "URGENT: Your account will be suspended. Click here immediately to verify.";
        MultipartFile phishingFile = new MockMultipartFile(
                "file", "urgent.txt", "text/plain", phishingContent.getBytes()
        );

        CustomDataException exception = assertThrows(CustomDataException.class,
                () -> fileSecurityValidator.validateFileSecurity(phishingFile));

        assertEquals(ErrorCodes.PHISHING_CONTENT_DETECTED.getErrorCode(), exception.getErrorCode());
    }
}
