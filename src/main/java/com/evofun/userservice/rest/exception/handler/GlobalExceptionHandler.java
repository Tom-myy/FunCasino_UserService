package com.evofun.userservice.rest.exception.handler;

import com.evofun.userservice.common.error.*;
import com.evofun.userservice.rest.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /* //seems unused...
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto(
                        ErrorCode.USERNAME_NOT_FOUND,
                        null,
                        ex.getMessage(),
                        null)
                );
    }*/

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDto> handleBadCredentials(InvalidCredentialsException ex) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.BUS);

        log.info("⚠️ {} (errorId: '{}')", ex.getDeveloperMessage(), errorId);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto(
                        ErrorCode.BAD_CREDENTIALS,
                        errorId,
                        ex.getUserMessage(),
                        null)
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorDto> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new FieldErrorDto(err.getField(), err.getDefaultMessage()))
                .toList();

        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.VAL);

        log.info("⚠️ {} (errorId: '{}')", ex.getMessage(), errorId);

        return ResponseEntity.badRequest().body(
                new ErrorDto(
                        ErrorCode.VALIDATION_ERROR,
                        errorId,
                        "Validation failed",
                        errors
                )
        );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleAlreadyExists(AlreadyExistsException ex) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.BUS);

//        log.info("⚠️ {} (errorId: '{}')", ex.getMessage(), errorId); вроде как бессмысленно логать... ну или в DEBUG засунуть

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorDto(
                        ErrorCode.ALREADY_EXISTS,
                        errorId,
                        "Such field(s) already exist(s):",
                        ex.getErrors())
                );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFoundException(UserNotFoundException ex) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.BUS);

        log.error("⚠️ {} (errorId: '{}')", ex.getDeveloperMessage(), errorId, ex);

        log.warn(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(
                        ErrorCode.USER_NOT_FOUND,
                        errorId,
                        ex.getUserMessage(),
                        null)
                );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ValidationException ex) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.BUS);

        log.info("⚠️ Business logic violation detected (errorId: '{}'), msg: '{}'", errorId, ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorDto(
                        ErrorCode.VALIDATION_ERROR,
                        errorId,
                        ex.getUserMessage(),
                        null)
                );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> handle404(NoHandlerFoundException ex, HttpServletRequest request) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.API);

        log.info("⚠️ 404 No Handler Found: method='{}', path='{}' (errorId: {})",
                request.getMethod(),
                request.getRequestURI(),
                errorId);

        String uri = ex.getRequestURL();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        errorId,
                        "Endpoint not found with request URI '" + uri + "'.",
                        null)
                );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDto> handle405(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.API);

        log.info("⚠️ 405 Method Not Allowed: method='{}', path='{}' (errorId: {})",
                request.getMethod(),
                request.getRequestURI(),
                errorId);


        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorDto(
                        ErrorCode.METHOD_NOT_SUPPORTED,
                        errorId,
                        "Method '" + request.getMethod() + "' not supported for this endpoint.",
                        null)
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleInvalidJson(HttpMessageNotReadableException ex) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.JSON);
        log.info("⚠️ Invalid JSON format (errorId: '{}'), msg: '{}'", errorId, ex.getMostSpecificCause().getMessage());

        String userMessage = "Invalid JSON input. Please check for syntax errors (e.g., missing values, commas, or quotes).";
        return ResponseEntity
                .badRequest()
                .body(new ErrorDto(
                        ErrorCode.INVALID_JSON_FORMAT,
                        errorId,
                        userMessage,
                        null)
                );
    }

    @ExceptionHandler(ServiceUnavailable.class)
    public ResponseEntity<ErrorDto> handleInvalidJson(ServiceUnavailable ex) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.SYS);
        log.warn("⚠️ {} (ErrorId: '{}')", ex.getMessage(), errorId);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorDto(
                        ErrorCode.SERVICE_UNAVAILABLE,
                        errorId,
                        ex.getUserMessage(),
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnexpectedException(Exception ex) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.UNKNOWN);

        log.error("❌ Unexpected REST error (errorId: '{}'), msg: '{}'", errorId, ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(
                        ErrorCode.UNKNOWN_ERROR,
                        errorId,
                        "Internal server error. Please contact support.",
                        null)
                );
    }
}