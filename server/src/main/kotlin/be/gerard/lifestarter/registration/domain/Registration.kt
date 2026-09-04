package be.gerard.lifestarter.registration.domain

import java.time.Instant

/**
 * The aggregate root: one household's RSVP, identified by the e-mail address it was filed under.
 */
data class Registration(
    val email: String,
    val guests: List<Guest>,
    val contactOptions: List<ContactOption> = emptyList(),
    val pledgeName: String? = null,
    val activities: List<Activity> = emptyList(),
    val registeredAt: Instant,
) {
    init {
        require(email.isNotBlank()) { "registration.email must not be blank" }
        require(guests.isNotEmpty()) { "registration must have at least one guest" }
    }

    val guestCount: Int
        get() = guests.size

    fun attends(activity: Activity): Boolean = activity in activities

    /** Every mailbox that agreed to be contacted by e-mail. */
    fun mailboxes(): List<String> = contactOptions.mapNotNull(ContactOption::mailbox).distinct()
}
