package be.gerard.lifestarter.registration.domain

/** Raised once a [Registration] has been durably stored. */
data class RegistrationAdded(
    val registration: Registration,
)

/**
 * Outbound port for announcing domain events.
 *
 * Keeps the domain free of any knowledge about Spring's application event infrastructure.
 */
fun interface RegistrationEventPublisher {
    fun publish(event: RegistrationAdded)
}
