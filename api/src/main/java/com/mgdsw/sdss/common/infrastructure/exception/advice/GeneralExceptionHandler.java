/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.infrastructure.exception.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAccessDeniedException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAlreadyExistException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomForbiddenException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomHystrixRuntimeException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomServiceException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomServiceNotAvailableException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomValidationException;
import com.mgdsw.sdss.common.infrastructure.exception.dto.DetailedErrorResponseAbstract;
import com.mgdsw.sdss.common.infrastructure.exception.dto.ErrorResponse;
import com.mgdsw.sdss.common.infrastructure.success.AbstractBaseResponse;
import com.mgdsw.sdss.common.security.SecureErrorHandler;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Log4j2
@Hidden
@ControllerAdvice
@RequiredArgsConstructor
public class GeneralExceptionHandler {

    private final SecureErrorHandler secureErrorHandler;

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({CustomAuthorizationException.class, AuthenticationException.class})
    public ResponseEntity<AbstractBaseResponse> handle(CustomAuthorizationException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), exception.getErrorCode(), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<AbstractBaseResponse> handle(CustomAccessDeniedException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), exception.getErrorCode(), HttpStatus.CONFLICT);
    }

    /**
     * @param exception CustomNotFoundException
     * @return ResponseEntity<ErrorDetails>
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({CustomNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<AbstractBaseResponse> handle(CustomNotFoundException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), exception.getErrorCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * @param exception CustomServiceException
     * @return ResponseEntity<ErrorDetails>
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomServiceException.class)
    public ResponseEntity<AbstractBaseResponse> handle(CustomServiceException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), exception.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(CustomForbiddenException.class)
    public ResponseEntity<AbstractBaseResponse> handle(CustomForbiddenException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), exception.getErrorCode(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({CustomServiceNotAvailableException.class})
    public ResponseEntity<AbstractBaseResponse> handle(CustomServiceNotAvailableException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), exception.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({CustomHystrixRuntimeException.class})
    public ResponseEntity<AbstractBaseResponse> handle(CustomHystrixRuntimeException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), exception.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AbstractBaseResponse> handle(BadCredentialsException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), ErrorCodes.UNAUTHORIZED.getErrorCode(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * @param exception CustomDataException
     * @return ResponseEntity<ErrorDetails>
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomDataException.class)
    public ResponseEntity<AbstractBaseResponse> handle(CustomDataException exception) {
        secureErrorHandler.logErrorSecurely(exception.getMessage(), exception);
        String sanitizedMessage = secureErrorHandler.sanitizeErrorMessage(exception.getMessage());
        return this.createGlobalException(sanitizedMessage, exception.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomAlreadyExistException.class)
    public ResponseEntity<AbstractBaseResponse> handle(CustomAlreadyExistException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), exception.getErrorCode(), HttpStatus.CONFLICT);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<AbstractBaseResponse> handle(CustomValidationException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(), exception.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 500 - Handle http not writable from a spring framework
     */
    @ExceptionHandler(HttpMessageNotWritableException.class)
    @NonNull
    public ResponseEntity<Object> handleHttpMessageNotWritable(@NonNull HttpMessageNotWritableException ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(ErrorResponse.builder().message(ErrorCodes.ERROR_WRITING_JSON.getMessage())
                .errorCode(ErrorCodes.ERROR_WRITING_JSON.getErrorCode()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle http not readable
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @NonNull
    public ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex, @NonNull HttpStatus status) {
        log.error(Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(ErrorResponse.builder().message(ErrorCodes.ERROR_READING_JSON.getMessage())
                .errorCode(ErrorCodes.ERROR_READING_JSON.getErrorCode()).build(), status);
    }

    /**
     * 405 – Handle request method isn't supported
     */
    @NonNull
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(@NonNull HttpRequestMethodNotSupportedException ex,
                                                                      @NonNull HttpStatus status) {
        log.error(Arrays.toString(ex.getStackTrace()));
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod()).append(" method is not supported for this request. Supported methods are ");
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(Optional.ofNullable(ex.getSupportedHttpMethods()).map(builder::append).toString())
                .errorCode(ErrorCodes.METHOD_NOT_SUPPORTED.getErrorCode()).build(), status);
    }

    /**
     * 400 - ConstraintViolationException from java APIs
     *
     * @param exception ConstraintViolationException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<AbstractBaseResponse> handle(ConstraintViolationException exception) {
        secureErrorHandler.logErrorSecurely(exception.getMessage(), exception);

        // Provide a generic error message for validation failures
        String genericMessage = "Invalid input provided. Please check your request parameters and try again.";

        return this.createGlobalException(
                genericMessage,
                ErrorCodes.BAD_REQUEST.getErrorCode(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<AbstractBaseResponse> handle(MissingRequestHeaderException exception, HttpStatus status) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(
                exception.getHeaderName() + " parameter is missing", ErrorCodes.REQUEST_PARAMETER_MISSING.getErrorCode(), status);
    }

    /**
     * 406 - No Handler Found Exception
     */

    @NonNull
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(@NonNull NoHandlerFoundException ex,
                                                                @NonNull HttpStatus status) {
        log.error(Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(ErrorResponse.builder().message("No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL())
                .errorCode(ErrorCodes.HANDLER_NOT_FOUND.getErrorCode()).build(), status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AbstractBaseResponse> handle(AccessDeniedException ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        log.info("Access Denied Message -> {}", ex.getMessage());
        return unauthorizedResponse();
    }

    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<AbstractBaseResponse> handle(MaxUploadSizeExceededException ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        return this.createGlobalException(ex.getMessage(),
                ErrorCodes.MAX_UPLOAD_SIZE_EXCEEDED_EXCEPTION.getErrorCode(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * @param exception SQLException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<AbstractBaseResponse> handle(SQLException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(exception.getMessage(),
                ErrorCodes.SQL_EXCEPTION.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle BindException.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<AbstractBaseResponse> handleBindException(BindException ex, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error ->
                errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(errors.entrySet().iterator().next().getValue())
                        .errorCode(ErrorCodes.BAD_REQUEST.getErrorCode())
                        .uriPath(request.getDescription(false).substring(4))
                        .build(), HttpStatus.BAD_REQUEST);
    }

    @NonNull
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error ->
                errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        log.error("BindingResult Error message :{}, {}", exception.getMessage(), exception.getStackTrace());
        return new ResponseEntity<>(ErrorResponse.builder().message(errors.entrySet().iterator().next().getValue())
                .errorCode(ErrorCodes.BAD_REQUEST.getErrorCode()).build(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 406 - method arguments not valid from spring mvc
     */
    @NonNull
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameter(@NonNull MissingServletRequestParameterException ex,
                                                                       @NonNull HttpStatus status) {
        log.error(Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(ErrorResponse.builder().message(ex.getParameterName() + " parameter missing")
                .errorCode(ErrorCodes.REQUEST_PARAMETER_MISSING.getErrorCode()).build(), status);
    }

    /**
     * @param exception NoSuchElementException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<AbstractBaseResponse> handle(NoSuchElementException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(
                exception.getMessage(), ErrorCodes.NOT_FOUND.getErrorCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * @param exception DuplicateKeyException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<AbstractBaseResponse> handle(DuplicateKeyException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(
                exception.getMessage(), ErrorCodes.CONFLICT.getErrorCode(), HttpStatus.CONFLICT);
    }

    /**
     * @param exception IllegalArgumentException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AbstractBaseResponse> handle(IllegalArgumentException exception) {
        secureErrorHandler.logErrorSecurely(exception.getMessage(), exception);

        // Provide a generic error message for invalid arguments
        String genericMessage = "Invalid input provided. Please check your request parameters and try again.";

        return this.createGlobalException(
                genericMessage,
                ErrorCodes.BAD_REQUEST.getErrorCode(),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * @param exception ValidationException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<AbstractBaseResponse> handle(ValidationException exception) {
        log.error(Arrays.toString(exception.getStackTrace()));
        return this.createGlobalException(
                exception.getMessage(), ErrorCodes.BAD_REQUEST.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle NumberFormatException to prevent information disclosure
     *
     * @param exception NumberFormatException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<AbstractBaseResponse> handle(NumberFormatException exception) {
        secureErrorHandler.logErrorSecurely(exception.getMessage(), exception);
        return this.createGlobalException(
                "Invalid number format. Please provide a valid numeric value.",
                ErrorCodes.BAD_REQUEST.getErrorCode(),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle TypeMismatchException to prevent information disclosure
     * This handles cases where Spring can't convert request parameters to expected types
     *
     * @param exception TypeMismatchException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<AbstractBaseResponse> handle(TypeMismatchException exception) {
        secureErrorHandler.logErrorSecurely(exception.getMessage(), exception);

        // Provide a generic error message without exposing internal details
        String genericMessage = "Invalid parameter format. Please check your input and try again.";

        return this.createGlobalException(
                genericMessage,
                ErrorCodes.BAD_REQUEST.getErrorCode(),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * @param exception InvalidFormatException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<AbstractBaseResponse> handle(InvalidFormatException exception) {
        log.debug(exception.toString(), exception);
        log.error("InvalidFormatException message {}, {}", exception.getMessage(), exception.getStackTrace());
        List<String> errorsList = new ArrayList<>();
        Class<?> type = exception.getTargetType();
        if (type != null) {
            return new ResponseEntity<>(
                    new DetailedErrorResponseAbstract<>("Invalid request", HttpStatus.BAD_REQUEST.toString(),
                            exception.getMessage(), Collections.singletonList(
                            "The parameter of '" + type.getSimpleName() + " must have a value among:"
                                    + StringUtils.join(type.getEnumConstants(), ", "))), HttpStatus.BAD_REQUEST);
        }
        exception.getPath().forEach(reference -> errorsList.add("Value of '" + reference.getFieldName() + " not a valid representation"));
        return new ResponseEntity<>(
                new DetailedErrorResponseAbstract<>("Invalid request", HttpStatus.BAD_REQUEST.toString(),
                        exception.getMessage(), errorsList), HttpStatus.BAD_REQUEST);
    }

    /**
     * @param exception PersistenceException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<AbstractBaseResponse> handle(PersistenceException exception) {
        log.error("Error message :{}, {}", exception.getMessage(), exception.getStackTrace());
        if (exception.getCause() instanceof ConstraintViolationException) {
            return this.createGlobalException(exception.getCause().getCause().getMessage(),
                    ErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return handleException(exception);
        }
    }

    /**
     * @param exception DataIntegrityViolationException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<AbstractBaseResponse> handle(DataIntegrityViolationException exception) {
        log.error("DataIntegrityViolationException message ----> {}, {}", exception.getMessage(), exception.getStackTrace());
        return this.createGlobalException(exception.getCause().getCause().getMessage(),
                ErrorCodes.DATA_INTEGRITY_VIOLATION_EXCEPTION.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    /**
     * @param exception RuntimeException
     * @return AbstractBaseResponse
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AbstractBaseResponse> handle(RuntimeException exception) {
        secureErrorHandler.logErrorSecurely(exception.getMessage(), exception);
        String sanitizedMessage = secureErrorHandler.sanitizeErrorMessage(exception.getMessage());
        return this.createGlobalException(
                sanitizedMessage, ErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 - Internal server error
     *
     * @param exception Exception
     * @return AbstractBaseResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AbstractBaseResponse> handleException(Exception exception) {
        secureErrorHandler.logErrorSecurely(exception.getMessage(), exception);
        String sanitizedMessage = secureErrorHandler.sanitizeErrorMessage(exception.getMessage());
        return this.createGlobalException(
                sanitizedMessage, ErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<AbstractBaseResponse> unauthorizedResponse() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder().message(ErrorCodes.ACCESS_DENIED.getMessage())
                .errorCode(ErrorCodes.ACCESS_DENIED.getErrorCode()).build());
    }

    private ResponseEntity<AbstractBaseResponse> createGlobalException(String errorMessage, String errorCode, HttpStatus httpStatus) {
        return new ResponseEntity<>(ErrorResponse.builder().message(errorMessage).errorCode(errorCode).build(), httpStatus);
    }
}
