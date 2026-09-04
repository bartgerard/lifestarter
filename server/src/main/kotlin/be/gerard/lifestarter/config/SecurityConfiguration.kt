package be.gerard.lifestarter.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import java.util.UUID

@ConfigurationProperties(prefix = "lifestarter.security")
data class SecurityProperties(
    val admin: AdminAccount = AdminAccount(),
) {
    data class AdminAccount(
        val username: String = "admin",
        /** Left empty on purpose: a random password is generated and logged when none is supplied. */
        val password: String = "",
    )
}

/**
 * Everything the RSVP flow needs is public; the organiser-only views require the ADMIN role.
 *
 * CSRF is disabled because the API is stateless JSON consumed by a separate SPA with HTTP Basic —
 * there is no cookie-backed session for an attacker to ride.
 */
@Configuration(proxyBeanMethods = false)
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(
        properties: SecurityProperties,
        passwordEncoder: PasswordEncoder,
    ): UserDetailsService {
        val admin = properties.admin
        val password = admin.password.ifBlank {
            UUID.randomUUID().toString().also {
                log.warn(
                    "No lifestarter.security.admin.password configured. " +
                        "Generated password for user '{}': {}",
                    admin.username,
                    it,
                )
            }
        }

        return InMemoryUserDetailsManager(
            User.withUsername(admin.username)
                .password(passwordEncoder.encode(password))
                .roles(ADMIN)
                .build(),
        )
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { it.disable() }
        .cors { }
        .authorizeHttpRequests { requests ->
            requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Only the export leaks personal data. The aggregate counters are what the public
                // RSVP page itself displays, so they stay open.
                .requestMatchers("/api/exports/**").hasRole(ADMIN)
                .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole(ADMIN)
                .anyRequest().permitAll()
        }
        .httpBasic { }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .build()

    private companion object {
        const val ADMIN = "ADMIN"
        val log = LoggerFactory.getLogger(SecurityConfiguration::class.java)
    }
}
