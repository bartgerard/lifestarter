package be.gerard.lifestarter.config

import be.gerard.lifestarter.allergy.domain.AllergyRepository
import be.gerard.lifestarter.allergy.domain.AllergyService
import be.gerard.lifestarter.pledge.domain.PledgeRepository
import be.gerard.lifestarter.pledge.domain.PledgeService
import be.gerard.lifestarter.wave.domain.WaveRepository
import be.gerard.lifestarter.wave.domain.WaveService
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "lifestarter.reference-data")
data class ReferenceDataProperties(
    val enabled: Boolean = true,
)

/**
 * Seeds the catalogues the RSVP form needs, through the domain ports.
 *
 * Going through the ports rather than through SQL migrations means SQLite and MongoDB end up with
 * exactly the same content. Every `saveAll` is an insert-if-absent, so restarts are harmless and
 * manual edits are never overwritten.
 */
@Component
@ConditionalOnProperty(prefix = "lifestarter.reference-data", name = ["enabled"], matchIfMissing = true)
class ReferenceDataInitializer(
    private val allergies: AllergyRepository,
    private val pledges: PledgeRepository,
    private val waves: WaveRepository,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        allergies.saveAll(AllergyService.DEFAULTS)
        pledges.saveAll(PledgeService.DEFAULTS)
        waves.saveAll(WaveService.DEFAULTS)

        log.info(
            "Reference data ready: {} allergies, {} pledges, {} waves",
            AllergyService.DEFAULTS.size,
            PledgeService.DEFAULTS.size,
            WaveService.DEFAULTS.size,
        )
    }

    private companion object {
        val log = LoggerFactory.getLogger(ReferenceDataInitializer::class.java)
    }
}
