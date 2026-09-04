package be.gerard.lifestarter.config

import be.gerard.lifestarter.access.domain.BouncerRepository
import be.gerard.lifestarter.access.domain.VipRepository
import be.gerard.lifestarter.access.repository.MongoBouncerRepository
import be.gerard.lifestarter.access.repository.MongoVipRepository
import be.gerard.lifestarter.allergy.domain.AllergyRepository
import be.gerard.lifestarter.allergy.repository.MongoAllergyRepository
import be.gerard.lifestarter.pledge.domain.PledgeRepository
import be.gerard.lifestarter.pledge.repository.MongoPledgeRepository
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import be.gerard.lifestarter.registration.repository.MongoRegistrationRepository
import be.gerard.lifestarter.wave.domain.WaveRepository
import be.gerard.lifestarter.wave.repository.MongoWaveRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoOperations

/**
 * Wires the MongoDB adapters.
 *
 * Spring Data repository interfaces are deliberately not used: the adapters talk to
 * [MongoOperations] directly so `*Record` documents never escape this layer.
 */
@Configuration(proxyBeanMethods = false)
@Profile(PersistenceProfiles.MONGODB)
class MongoPersistenceConfiguration : InitializingBean {

    override fun afterPropertiesSet() {
        log.info("Using MongoDB persistence")
    }

    @Bean
    fun allergyRepository(mongo: MongoOperations): AllergyRepository = MongoAllergyRepository(mongo)

    @Bean
    fun pledgeRepository(mongo: MongoOperations): PledgeRepository = MongoPledgeRepository(mongo)

    @Bean
    fun waveRepository(mongo: MongoOperations): WaveRepository = MongoWaveRepository(mongo)

    @Bean
    fun registrationRepository(mongo: MongoOperations): RegistrationRepository =
        MongoRegistrationRepository(mongo)

    @Bean
    fun vipRepository(mongo: MongoOperations): VipRepository = MongoVipRepository(mongo)

    @Bean
    fun bouncerRepository(mongo: MongoOperations): BouncerRepository = MongoBouncerRepository(mongo)

    private companion object {
        val log = LoggerFactory.getLogger(MongoPersistenceConfiguration::class.java)
    }
}
