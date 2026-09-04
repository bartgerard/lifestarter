package be.gerard.lifestarter.registration.domain

/** How a registrant prefers to be contacted. */
enum class ContactMethod {
    NONE,
    EMAIL,
    PIGEON,
    PHONE,
}

/**
 * One way of reaching the people behind a [Registration].
 *
 * Every field is optional because the RSVP form only asks for what the chosen [contactMethod]
 * actually needs.
 */
data class ContactOption(
    val email: String? = null,
    val address: String? = null,
    val zipCode: String? = null,
    val city: String? = null,
    val countryIso3: String? = null,
    val phoneNumber: String? = null,
    val contactMethod: ContactMethod = ContactMethod.NONE,
) {
    /** The e-mail address to write to, or `null` when this option is not a usable mailbox. */
    val mailbox: String?
        get() = email?.takeIf { contactMethod == ContactMethod.EMAIL && it.contains('@') }
}
