package be.gerard.lifestarter.pledge.adapter

import be.gerard.lifestarter.pledge.domain.PledgeSubscriptionCounter
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import org.springframework.stereotype.Component

/**
 * Counts pledge subscriptions from the registrations.
 *
 * Implementing the port here keeps the pledge domain from importing the registration domain.
 */
@Component
class RegistrationPledgeSubscriptionCounter(
    private val registrations: RegistrationRepository,
) : PledgeSubscriptionCounter {

    override fun countGuestsPerPledge(): Map<String, Int> = registrations.findAll()
        .filter { it.pledgeName != null }
        .groupingBy { requireNotNull(it.pledgeName) }
        .fold(0) { count, registration -> count + registration.guestCount }
}
