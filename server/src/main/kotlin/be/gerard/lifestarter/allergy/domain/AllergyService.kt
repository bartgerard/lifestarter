package be.gerard.lifestarter.allergy.domain

class AllergyService(
    private val allergies: AllergyRepository,
) {
    fun findAll(): List<Allergy> = allergies.findAll().sortedBy(Allergy::id)

    companion object {
        /** The allergies offered by the RSVP form out of the box. */
        val DEFAULTS: List<Allergy> = listOf(
            "EGGS",
            "MILK",
            "PEANUTS",
            "TREE_NUTS",
            "FISH",
            "SHELLFISH",
            "WHEAT",
            "SOY",
            "PAPRIKA",
            "PAPRIKA_POWDER",
        ).map(::Allergy)
    }
}
