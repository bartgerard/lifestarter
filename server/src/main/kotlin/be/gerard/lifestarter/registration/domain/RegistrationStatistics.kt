package be.gerard.lifestarter.registration.domain

/** Aggregated counts over all registrations, for the RSVP progress display and the admin view. */
data class RegistrationStatistics(
    val totalRegistrations: Int,
    val totalGuests: Int,
    val dinnerGuests: Int,
    val guestsPerPledge: Map<String, Int>,
) {
    companion object {
        fun of(registrations: List<Registration>): RegistrationStatistics = RegistrationStatistics(
            totalRegistrations = registrations.size,
            totalGuests = registrations.sumOf(Registration::guestCount),
            dinnerGuests = registrations.filter { it.attends(Activity.DINNER) }
                .sumOf(Registration::guestCount),
            guestsPerPledge = registrations.filter { it.pledgeName != null }
                .groupingBy { requireNotNull(it.pledgeName) }
                .fold(0) { count, registration -> count + registration.guestCount },
        )
    }
}
