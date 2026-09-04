package be.gerard.lifestarter.allergy.domain

/** A food allergy a guest can declare, identified by its stable technical code. */
data class Allergy(
    val id: String,
) {
    init {
        require(id.isNotBlank()) { "allergy.id must not be blank" }
    }
}
