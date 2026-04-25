package t.lab.guide.exception

import org.springframework.http.HttpStatus

class BadRequestException(
    message: String,
    cause: Throwable? = null,
) : ApiException(message, HttpStatus.BAD_REQUEST, cause)
