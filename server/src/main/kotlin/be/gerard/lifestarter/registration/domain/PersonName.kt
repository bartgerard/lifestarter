package be.gerard.lifestarter.registration.domain

/**
 * A person's name, used to match guests against the VIP and bouncer lists.
 *
 * Matching is case-insensitive and whitespace-insensitive, so the canonical [normalised] form is
 * what adapters should persist and query on.
 */
data class PersonName(
    val firstName: String,
    val lastName: String,
) {
    init {
        require(firstName.isNotBlank()) { "firstName must not be blank" }
        require(lastName.isNotBlank()) { "lastName must not be blank" }
    }

    val fullName: String
        get() = "$firstName $lastName"

    fun normalised(): PersonName = PersonName(
        firstName = firstName.trim().lowercase(),
        lastName = lastName.trim().lowercase(),
    )

    companion object {
        /** Builds a name, or `null` when either part is missing — convenient for optional inputs. */
        fun ofNullable(firstName: String?, lastName: String?): PersonName? =
            if (firstName.isNullOrBlank() || lastName.isNullOrBlank()) {
                null
            } else {
                PersonName(firstName, lastName)
            }
    }
}
