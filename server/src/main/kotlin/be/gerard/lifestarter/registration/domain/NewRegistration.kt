package be.gerard.lifestarter.registration.domain

/** Everything needed to file a new RSVP; the timestamp is the domain's business, not the caller's. */
data class NewRegistration(
    val email: String,
    val guests: List<Guest>,
    val contactOptions: List<ContactOption> = emptyList(),
    val pledgeName: String? = null,
    val activities: List<Activity> = emptyList(),
) {
    init {
        require(email.isNotBlank()) { "registration.email must not be blank" }
        require(guests.isNotEmpty()) { "registration must have at least one guest" }
    }
}
