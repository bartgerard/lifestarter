package be.gerard.lifestarter.registration.domain

/** A single person attending, as declared on a [Registration]. */
data class Guest(
    val name: PersonName,
    val role: String? = null,
    val diet: Diet? = null,
    val allergies: List<String> = emptyList(),
    val comment: String? = null,
) {
    val firstName: String
        get() = name.firstName

    val lastName: String
        get() = name.lastName
}
