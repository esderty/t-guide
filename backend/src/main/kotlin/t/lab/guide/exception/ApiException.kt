package t.lab.guide.exception

import org.springframework.http.HttpStatus

open class ApiException(
    message: String,
    val status: HttpStatus,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
