package be.gerard.lifestarter.wave.domain

import java.time.LocalDate

/** An RSVP round: guests are asked to reply by [deadline] (inclusive). */
data class Wave(
    val label: String,
    val deadline: LocalDate,
) {
    init {
        require(label.isNotBlank()) { "wave.label must not be blank" }
    }

    fun isOpenOn(date: LocalDate): Boolean = !date.isAfter(deadline)
}
