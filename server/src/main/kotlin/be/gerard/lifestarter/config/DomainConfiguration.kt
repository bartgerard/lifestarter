package be.gerard.lifestarter.config

import be.gerard.lifestarter.access.domain.AccessService
import be.gerard.lifestarter.access.domain.BouncerRepository
import be.gerard.lifestarter.access.domain.VipRepository
import be.gerard.lifestarter.allergy.domain.AllergyRepository
import be.gerard.lifestarter.allergy.domain.AllergyService
import be.gerard.lifestarter.country.domain.CountryCatalog
import be.gerard.lifestarter.country.domain.CountryService
import be.gerard.lifestarter.export.domain.ExportService
import be.gerard.lifestarter.export.domain.RegistrationExporter
import be.gerard.lifestarter.pledge.domain.PledgeRepository
import be.gerard.lifestarter.pledge.domain.PledgeService
import be.gerard.lifestarter.pledge.domain.PledgeSubscriptionCounter
import be.gerard.lifestarter.registration.domain.RegistrationEventPublisher
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import be.gerard.lifestarter.registration.domain.RegistrationService
import be.gerard.lifestarter.wave.domain.WaveRepository
import be.gerard.lifestarter.wave.domain.WaveService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 * Registers the domain services as beans.
 *
 * Doing it here — rather than with `@Service` on the classes themselves — is what keeps the
 * `domain` packages entirely free of framework imports.
 */
@Configuration(proxyBeanMethods = false)
class DomainConfiguration {

    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()

    @Bean
    fun registrationService(
        registrations: RegistrationRepository,
        eventPublisher: RegistrationEventPublisher,
        clock: Clock,
    ): RegistrationService = RegistrationService(registrations, eventPublisher, clock)

    @Bean
    fun allergyService(allergies: AllergyRepository): AllergyService = AllergyService(allergies)

    @Bean
    fun pledgeService(
        pledges: PledgeRepository,
        subscriptions: PledgeSubscriptionCounter,
    ): PledgeService = PledgeService(pledges, subscriptions)

    @Bean
    fun waveService(waves: WaveRepository, clock: Clock): WaveService = WaveService(waves, clock)

    @Bean
    fun countryService(catalog: CountryCatalog): CountryService = CountryService(catalog)

    @Bean
    fun accessService(vips: VipRepository, bouncers: BouncerRepository): AccessService =
        AccessService(vips, bouncers)

    @Bean
    fun exportService(
        registrations: RegistrationRepository,
        bouncers: BouncerRepository,
        exporter: RegistrationExporter,
        clock: Clock,
    ): ExportService = ExportService(registrations, bouncers, exporter, clock)
}
