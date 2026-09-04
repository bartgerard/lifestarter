package be.gerard.lifestarter.registration.adapter

import be.gerard.lifestarter.registration.domain.RegistrationAdded
import be.gerard.lifestarter.registration.domain.RegistrationEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/** Bridges the domain's event port onto Spring's application event infrastructure. */
@Component
class SpringRegistrationEventPublisher(
    private val delegate: ApplicationEventPublisher,
) : RegistrationEventPublisher {

    override fun publish(event: RegistrationAdded) = delegate.publishEvent(event)
}
