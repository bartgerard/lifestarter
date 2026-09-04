package be.gerard.lifestarter.wave.domain

import java.time.Clock
import java.time.LocalDate

class WaveService(
    private val waves: WaveRepository,
    private val clock: Clock,
) {
    fun findAll(): List<Wave> = waves.findAll().sortedBy(Wave::deadline)

    /** The next wave still accepting replies, or `null` once every deadline has passed. */
    fun currentWave(): Wave? {
        val today = LocalDate.now(clock)

        return waves.findAll()
            .filter { it.isOpenOn(today) }
            .minByOrNull(Wave::deadline)
    }

    companion object {
        /** The RSVP rounds this wedding ships with. */
        val DEFAULTS: List<Wave> = listOf(
            Wave("1st", LocalDate.of(2019, 3, 1)),
            Wave("2nd", LocalDate.of(2019, 4, 1)),
            Wave("3rd", LocalDate.of(2019, 4, 20)),
        )
    }
}
