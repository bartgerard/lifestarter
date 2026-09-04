package be.gerard.lifestarter.wave.domain

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

class WaveServiceTest {

    private val waves = mockk<WaveRepository>()

    @Test
    fun `picks the earliest deadline that has not passed`() {
        every { waves.findAll() } returns WaveService.DEFAULTS

        val service = WaveService(waves, clockOn("2019-03-15"))

        assertThat(service.currentWave()?.label).isEqualTo("2nd")
    }

    @Test
    fun `a wave stays open on its deadline day`() {
        every { waves.findAll() } returns WaveService.DEFAULTS

        val service = WaveService(waves, clockOn("2019-03-01"))

        assertThat(service.currentWave()?.label).isEqualTo("1st")
    }

    @Test
    fun `returns nothing once every deadline has passed`() {
        every { waves.findAll() } returns WaveService.DEFAULTS

        val service = WaveService(waves, clockOn("2020-01-01"))

        assertThat(service.currentWave()).isNull()
    }

    @Test
    fun `lists the waves chronologically`() {
        every { waves.findAll() } returns WaveService.DEFAULTS.reversed()

        val service = WaveService(waves, clockOn("2019-01-01"))

        assertThat(service.findAll().map(Wave::label)).containsExactly("1st", "2nd", "3rd")
    }

    private fun clockOn(date: String): Clock =
        Clock.fixed(LocalDate.parse(date).atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC)
}
