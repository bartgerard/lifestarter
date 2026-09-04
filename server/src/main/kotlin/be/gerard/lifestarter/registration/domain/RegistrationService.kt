package be.gerard.lifestarter.registration.domain

import java.time.Clock

/** Orchestrates the RSVP use cases. Framework-free by design. */
class RegistrationService(
    private val registrations: RegistrationRepository,
    private val eventPublisher: RegistrationEventPublisher,
    private val clock: Clock,
) {
    fun register(command: NewRegistration): Registration {
        val registration = Registration(
            email = command.email.trim(),
            guests = command.guests,
            contactOptions = command.contactOptions,
            pledgeName = command.pledgeName?.trim()?.takeIf(String::isNotEmpty),
            activities = command.activities.distinct().sorted(),
            registeredAt = clock.instant(),
        )

        val saved = registrations.save(registration)
        eventPublisher.publish(RegistrationAdded(saved))

        return saved
    }

    fun findAll(): List<Registration> = registrations.findAll()

    fun findByEmail(email: String): Registration? = registrations.findByEmail(email.trim())

    fun statistics(): RegistrationStatistics = RegistrationStatistics.of(registrations.findAll())
}
