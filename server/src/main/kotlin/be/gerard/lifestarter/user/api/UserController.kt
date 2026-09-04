package be.gerard.lifestarter.user.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** The caller as the server sees them; `authenticated = false` for anonymous visitors. */
data class CurrentUserTo(
    val name: String,
    val authenticated: Boolean,
    val roles: List<String>,
)

@RestController
@RequestMapping("users")
@Tag(name = "users", description = "Information about the calling user")
class UserController {

    @GetMapping("me")
    @Operation(summary = "Describe the currently authenticated user.")
    fun me(authentication: Authentication?): CurrentUserTo {
        if (authentication == null || !authentication.isAuthenticated) {
            return ANONYMOUS
        }

        return CurrentUserTo(
            name = authentication.name,
            authenticated = true,
            roles = authentication.authorities.mapNotNull { it.authority }.sorted(),
        )
    }

    private companion object {
        val ANONYMOUS = CurrentUserTo(name = "anonymous", authenticated = false, roles = emptyList())
    }
}
