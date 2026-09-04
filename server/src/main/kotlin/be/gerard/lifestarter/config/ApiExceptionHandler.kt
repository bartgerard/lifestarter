package be.gerard.lifestarter.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

/**
 * Turns failures into RFC 9457 problem documents.
 *
 * Only messages the domain raises on purpose are echoed back; anything unexpected is logged and
 * reported as a bare `500` so internals never leak to the browser.
 */
@RestControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(IllegalArgumentException::class)
    fun onIllegalArgument(exception: IllegalArgumentException): ProblemDetail =
        problem(HttpStatus.BAD_REQUEST, "Invalid request", exception.message.orEmpty(), "invalid-request")

    @ExceptionHandler(IllegalStateException::class)
    fun onIllegalState(exception: IllegalStateException): ProblemDetail =
        problem(HttpStatus.CONFLICT, "Conflicting request", exception.message.orEmpty(), "conflict")

    /**
     * Authentication and authorisation failures must reach Spring Security's
     * `ExceptionTranslationFilter`, which turns them into `401`/`403`. Swallowing them here would
     * silently downgrade every denial to a `500`.
     */
    @ExceptionHandler(AccessDeniedException::class, AuthenticationException::class)
    fun rethrowSecurityFailures(exception: RuntimeException): Nothing = throw exception

    @ExceptionHandler(Exception::class)
    fun onUnexpected(exception: Exception): ProblemDetail {
        log.error("Unhandled exception", exception)

        return problem(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            title = "Unexpected error",
            detail = "The request could not be completed.",
            type = "internal-error",
        )
    }

    private fun problem(
        status: HttpStatus,
        title: String,
        detail: String,
        type: String,
    ): ProblemDetail = ProblemDetail.forStatusAndDetail(status, detail).apply {
        this.title = title
        this.type = URI.create("https://lifestarter.gerard.be/problems/$type")
    }

    private companion object {
        val log = LoggerFactory.getLogger(ApiExceptionHandler::class.java)
    }
}
