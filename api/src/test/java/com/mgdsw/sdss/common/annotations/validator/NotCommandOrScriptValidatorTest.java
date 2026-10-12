package com.mgdsw.sdss.common.annotations.validator;

import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotCommandOrScriptValidatorTest {

    private NotCommandOrScriptValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = new NotCommandOrScriptValidator();
        NotCommandOrScript annotation = mock(NotCommandOrScript.class);
        validator.initialize(annotation);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
    }

    @Test
    void testValidInput_ShouldReturnTrue() {
        assertTrue(validator.isValid("Hello World", context));
        assertTrue(validator.isValid("This is a normal text message", context));
        assertTrue(validator.isValid("User feedback about the survey", context));
        assertTrue(validator.isValid("Used for statistical analysis and improving service inclusivity.", context));
        assertTrue(validator.isValid("What is your gender?", context));
        assertTrue(validator.isValid("Prefer not to say", context));
        assertTrue(validator.isValid("", context));
        assertTrue(validator.isValid(null, context));
        assertTrue(validator.isValid("   ", context));
    }

    @Test
    void testShellCommands_ShouldReturnFalse() {
        assertFalse(validator.isValid("rm -rf /", context));
        assertFalse(validator.isValid("sudo su -", context));
        assertFalse(validator.isValid("chmod 777 file", context));
        assertFalse(validator.isValid("shutdown now", context));
        assertFalse(validator.isValid("kill -9 1234", context));
    }

    @Test
    void testScriptExecution_ShouldReturnFalse() {
        assertFalse(validator.isValid("eval('malicious code')", context));
        assertFalse(validator.isValid("system('rm -rf /')", context));
        assertFalse(validator.isValid("exec('dangerous command')", context));
        assertFalse(validator.isValid("shell_exec('ls -la')", context));
    }

    @Test
    void testSqlInjection_ShouldReturnFalse() {
        assertFalse(validator.isValid("DROP TABLE users", context));
        assertFalse(validator.isValid("DELETE FROM accounts", context));
        assertFalse(validator.isValid("UNION SELECT * FROM passwords", context));
        assertFalse(validator.isValid("INSERT INTO admin VALUES", context));
    }

    @Test
    void testScriptTags_ShouldReturnFalse() {
        assertFalse(validator.isValid("<script>alert('xss')</script>", context));
        assertFalse(validator.isValid("javascript:alert('test')", context));
        assertFalse(validator.isValid("onload=alert('hack')", context));
        assertFalse(validator.isValid("onclick=malicious()", context));
    }

    @Test
    void testCommandInjection_ShouldReturnFalse() {
        assertFalse(validator.isValid("test; rm file", context));
        assertFalse(validator.isValid("input && del data", context));
        assertFalse(validator.isValid("command | rm -rf", context));
        assertFalse(validator.isValid("$(whoami)", context));
        assertFalse(validator.isValid("`ls -la`", context));
    }

    @Test
    void testFileSystemOperations_ShouldReturnFalse() {
        assertFalse(validator.isValid("../../etc/passwd", context));
        assertFalse(validator.isValid("cat /etc/shadow", context));
        assertFalse(validator.isValid("c:\\windows\\system32", context));
        assertFalse(validator.isValid("cmd.exe", context));
        assertFalse(validator.isValid("/bin/bash", context));
    }

    @Test
    void testNetworkOperations_ShouldReturnFalse() {
        assertFalse(validator.isValid("wget http://malicious.com", context));
        assertFalse(validator.isValid("curl -X POST", context));
        assertFalse(validator.isValid("ssh user@server", context));
        assertFalse(validator.isValid("telnet example.com", context));
    }

    @Test
    void testProcessOperations_ShouldReturnFalse() {
        assertFalse(validator.isValid("ps aux", context));
        assertFalse(validator.isValid("service apache2 stop", context));
        assertFalse(validator.isValid("systemctl disable firewall", context));
        assertFalse(validator.isValid("ps -ef | grep", context));
    }

    @Test
    void testCaseInsensitivity_ShouldReturnFalse() {
        assertFalse(validator.isValid("RM -RF /", context));
        assertFalse(validator.isValid("DROP table users", context));
        assertFalse(validator.isValid("EVAL('code')", context));
        assertFalse(validator.isValid("<SCRIPT>alert()</SCRIPT>", context));
    }

    @Test
    void testMixedContent_ShouldReturnFalse() {
        assertFalse(validator.isValid("This is normal text but contains rm -rf /", context));
        assertFalse(validator.isValid("Survey feedback: DROP TABLE users", context));
        assertFalse(validator.isValid("Hello <script>alert('xss')</script> world", context));
    }
}
