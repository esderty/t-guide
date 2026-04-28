package t.lab.guide.exception

import org.springframework.http.HttpStatus

class NotFoundException(
    message: String,
    cause: Throwable? = null,
) : ApiException(message, HttpStatus.NOT_FOUND, cause)
