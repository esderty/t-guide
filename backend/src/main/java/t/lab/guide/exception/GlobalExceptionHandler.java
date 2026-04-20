package t.lab.guide.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import t.lab.guide.dto.ApiErrorResponse;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException (ApiException e, HttpServletRequest request) {
        logClientError(e.getStatus(), request.getRequestURI(), e.getMessage());
        return build(e.getStatus(), e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException (
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ));
        logClientError(HttpStatus.BAD_REQUEST, request.getRequestURI(), errors.toString());
        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation (
            ConstraintViolationException e,
            HttpServletRequest request
    ) {
        Map<String, String> errors = e.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        cv -> cv.getPropertyPath().toString(),
                        cv -> cv.getMessage() == null ? "invalid" : cv.getMessage(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ));
        logClientError(HttpStatus.BAD_REQUEST, request.getRequestURI(), errors.toString());
        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException (Exception e, HttpServletRequest request) {
        log.error("Unexpected error: {}", request.getRequestURI(), e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
    }

    private void logClientError (HttpStatus status, String path, String message) {
        log.warn("Client error status={}, path={}, message={}", status.value(), path, message);
    }

    private ResponseEntity<ApiErrorResponse> build (HttpStatus status, String message, Map<String, String> errors) {
        return ResponseEntity.status(status).body(
                ApiErrorResponse.builder()
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .timestamp(OffsetDateTime.now())
                        .errors(errors)
                        .build()
        );
    }
}
