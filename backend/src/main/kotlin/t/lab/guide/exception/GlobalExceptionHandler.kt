package t.lab.guide.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import java.time.OffsetDateTime
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import t.lab.guide.dto.ApiErrorResponse

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(
        e: ApiException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> {
        logClientError(e.status, request.requestURI, e.message)
        return build(e.status, e.message ?: e.status.reasonPhrase, emptyMap())
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> {
        val errors =
            e.bindingResult.fieldErrors.associateTo(LinkedHashMap()) { fe ->
                fe.field to (fe.defaultMessage ?: "invalid")
            }
        logClientError(HttpStatus.BAD_REQUEST, request.requestURI, errors.toString())
        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        e: ConstraintViolationException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> {
        val errors =
            e.constraintViolations.associateTo(LinkedHashMap()) { cv ->
                cv.propertyPath.toString() to (cv.message ?: "invalid")
            }
        logClientError(HttpStatus.BAD_REQUEST, request.requestURI, errors.toString())
        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(
        e: BadCredentialsException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> {
        logClientError(HttpStatus.UNAUTHORIZED, request.requestURI, e.message)
        return build(HttpStatus.UNAUTHORIZED, "Invalid credentials", emptyMap())
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(
        e: JwtException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> {
        logClientError(HttpStatus.UNAUTHORIZED, request.requestURI, e.message)
        return build(HttpStatus.UNAUTHORIZED, "Invalid or expired token", emptyMap())
    }

    @ExceptionHandler(DisabledException::class)
    fun handleDisabled(
        e: DisabledException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> {
        logClientError(HttpStatus.FORBIDDEN, request.requestURI, e.message)
        return build(HttpStatus.FORBIDDEN, "Account is disabled", emptyMap())
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNotFound(request: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        logClientError(HttpStatus.NOT_FOUND, request.requestURI, "Resource not found")
        return build(HttpStatus.NOT_FOUND, "Resource not found", emptyMap())
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        e: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> {
        log.error("Unexpected error: {}", request.requestURI, e)
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", emptyMap())
    }

    private fun logClientError(
        status: HttpStatus,
        path: String,
        message: String?,
    ) {
        log.warn("Client error status={}, path={}, message={}", status.value(), path, message)
    }

    private fun build(
        status: HttpStatus,
        message: String,
        errors: Map<String, String>,
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(status).body(
            ApiErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
                timestamp = OffsetDateTime.now(),
                errors = errors,
            ),
        )
}
