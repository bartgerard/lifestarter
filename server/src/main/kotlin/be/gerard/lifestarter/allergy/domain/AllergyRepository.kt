package be.gerard.lifestarter.allergy.domain

/** Outbound port for the allergy catalogue. */
interface AllergyRepository {
    fun findAll(): List<Allergy>

    /** Inserts the allergies that do not exist yet; already-known ones are left untouched. */
    fun saveAll(allergies: List<Allergy>)
}
