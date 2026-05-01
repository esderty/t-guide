package t.lab.guide.exception

import org.springframework.http.HttpStatus

class InternalServerException(
    message: String,
) : ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR)
