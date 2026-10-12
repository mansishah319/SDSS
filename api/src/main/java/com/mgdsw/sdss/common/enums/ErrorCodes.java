/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    // General errors
    INTERNAL_SERVER_ERROR("ERR-001", "Something went wrong"),
    HANDLER_NOT_FOUND("ERR-002", "No handler found"),
    REQUEST_PARAMETER_MISSING("ERR-003", "Missing request parameter "),
    INVALID_ARGUMENTS("ERR-004", "Invalid method arguments "),
    CONSTRAINT_VIOLATION("ERR-005", "Validation error"),
    METHOD_NOT_SUPPORTED("ERR-006", "Method not supported"),
    ERROR_WRITING_JSON("ERR-007", "Error in writing JSON"),
    ERROR_READING_JSON("ERR-008", "Error in reading JSON"),
    BIND_EXCEPTION("ERR-009", "Binding Exception Error"),
    MAX_UPLOAD_SIZE_EXCEEDED_EXCEPTION("ERR-010", "this is over of the max size, Please check size and try again"),
    SQL_EXCEPTION("ERR-011", "there is an error with database validation"),
    DATA_INTEGRITY_VIOLATION_EXCEPTION("ERR-012", "Data Integrity Violation Exception"),
    CONSTRAINT_VIOLATION_EXCEPTION("ERR-013", "Constraint Violation Exception"),
    BAD_REQUEST("ERR-014", "Something went wrong"),
    ACCESS_DENIED("ERR-015", "Access denied."),
    CONFLICT("ERR-016", "Conflict"),
    NOT_FOUND("ERR-017", "Not found"),
    UNAUTHORIZED("ERR-018", "Unauthorized User"),
    INVALID_JWT("ERR-019", "Invalid JWT"),
    INVALID_TOKEN("ERR-020", "Invalid JWT"),
    UNAUTHORIZED_OR_INVALID_TOKEN("ERR-021", "Unauthorized User or invalid token"),
    UNSUPPORTED_MEDIA_TYPE("ERR-022", "Unsupported media type"),
    UNSUPPORTED_FILE_TYPE("ERR-023", "Unsupported file type"),
    UPLOADED_ERROR("ERR-024", "File not Uploaded"),
    TEMPLATE_ERROR("ERR-025", "Something went wrong while preparing the email template"),
    FILE_ERROR("ERR-026", "Can't reach this file"),
    UNIQUENESS_ERROR("ERR-027", "Error validating uniqueness"),
    TOKEN_IS_EXPIRED_ERROR("ERR-028", "Token is Expired or invalid Kindly login again"),
    SDSS_UNAUTHORIZED_USER("ERR-029", "SDSS User Unauthorized Or Not Active Check with Administration Team"),
    MALICIOUS_FILE_DETECTED("ERR-030", "File contains potentially malicious content and cannot be uploaded"),
    PHISHING_CONTENT_DETECTED("ERR-031", "File contains phishing content and cannot be uploaded for security reasons"),

    // For Service,
    NOT_FOUND_EXCEPTION("ENTITY-ERR-01", "Not found exception for this : "),

    NOT_FOUND_ERR("MSG-01", "This ID: %s Not exist."),
    NOT_FOUND_FIELD_ERR("MSG-01", "This %s ID: %s Not exist."),
    EMPTY_LIST("MSG-02", "No data received."),
    ALREADY_EXIST_WITH_SPECIFIC_DATA("MSG-03", "This entity with these data %s already exist."),
    FIELD_ALREADY_EXIST("MSG-04", "%s This field is an unique, and this field data %s is already exist"),
    MISSING_REQUEST_DATA("MSG-05", "This request with these attributes missing %s field"),
    NO_ACTIVE_USER_ERR("MSG-06", "No Active User with This Grp Number: %s."),
    USER_ALREADY_ACTIVATED_OR_NOT_INVITED_YET("MSG-07", "User with this GRP number: %s has already been activated or does not have any invitation."),
    ALREADY_EXIST("MSG-08", "This %s already Exist.");

    final String errorCode;
    final String message;
}
