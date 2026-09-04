package be.gerard.lifestarter.registration.domain

/**
 * Outbound port for storing registrations.
 *
 * Implemented once per persistence mode; the domain is unaware of which one is active.
 */
interface RegistrationRepository {
    fun findAll(): List<Registration>

    fun findByEmail(email: String): Registration?

    fun save(registration: Registration): Registration
}
