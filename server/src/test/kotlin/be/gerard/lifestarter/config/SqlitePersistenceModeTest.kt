package be.gerard.lifestarter.config

import be.gerard.lifestarter.allergy.domain.AllergyRepository
import be.gerard.lifestarter.allergy.domain.AllergyService
import be.gerard.lifestarter.pledge.domain.PledgeRepository
import be.gerard.lifestarter.pledge.domain.PledgeService
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import be.gerard.lifestarter.registration.repository.SqliteRegistrationRepository
import be.gerard.lifestarter.useThrowawaySqlite
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

/**
 * Proves the single persistence knob: setting `lifestarter.persistence.type` activates the matching
 * profile, which in turn decides which adapters exist.
 */
@SpringBootTest
class SqlitePersistenceModeTest(
    @Autowired private val environment: Environment,
    @Autowired private val registrations: RegistrationRepository,
    @Autowired private val allergies: AllergyRepository,
    @Autowired private val pledges: PledgeRepository,
) {

    @Test
    fun `activates the sqlite profile`() {
        assertThat(environment.activeProfiles).contains(PersistenceProfiles.SQLITE)
        assertThat(environment.activeProfiles).doesNotContain(PersistenceProfiles.MONGODB)
    }

    @Test
    fun `wires the sqlite adapters`() {
        assertThat(registrations).isInstanceOf(SqliteRegistrationRepository::class.java)
    }

    @Test
    fun `seeds the reference data idempotently`() {
        val before = allergies.findAll().size

        allergies.saveAll(AllergyService.DEFAULTS)

        assertThat(allergies.findAll()).hasSize(before)
        assertThat(pledges.findAll()).hasSize(PledgeService.DEFAULTS.size)
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun sqlite(registry: DynamicPropertyRegistry) = registry.useThrowawaySqlite("mode")
    }
}
