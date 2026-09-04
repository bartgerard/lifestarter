package be.gerard.lifestarter.wave.domain

/** Outbound port for the RSVP waves. */
interface WaveRepository {
    fun findAll(): List<Wave>

    /** Inserts the waves that do not exist yet; already-known ones are left untouched. */
    fun saveAll(waves: List<Wave>)
}
