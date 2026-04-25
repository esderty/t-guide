package t.lab.guide.exception

import org.springframework.http.HttpStatus

class ConflictException(
    message: String,
    cause: Throwable? = null,
) : ApiException(message, HttpStatus.CONFLICT, cause)
