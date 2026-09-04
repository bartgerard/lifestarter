package be.gerard.lifestarter.config

import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.Guest
import be.gerard.lifestarter.registration.domain.PersonName
import be.gerard.lifestarter.registration.domain.Registration
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import be.gerard.lifestarter.registration.repository.MongoRegistrationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.DockerClientFactory
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.mongodb.MongoDBContainer
import java.time.Instant

/**
 * The same hexagon, driven by the other adapter.
 *
 * Skipped — not failed — when Docker is unavailable, so `verify` stays green on a machine that
 * only ever runs the SQLite mode.
 */
@SpringBootTest(properties = ["lifestarter.persistence.type=mongodb"])
@Testcontainers(disabledWithoutDocker = true)
class MongoPersistenceModeTest(
    @Autowired private val environment: Environment,
    @Autowired private val registrations: RegistrationRepository,
) {

    @Test
    fun `activates the mongodb profile and its adapters`() {
        assertThat(environment.activeProfiles).contains(PersistenceProfiles.MONGODB)
        assertThat(environment.activeProfiles).doesNotContain(PersistenceProfiles.SQLITE)
        assertThat(registrations).isInstanceOf(MongoRegistrationRepository::class.java)
    }

    @Test
    fun `round-trips a registration through MongoDB`() {
        registrations.save(
            Registration(
                email = "ada@example.com",
                guests = listOf(Guest(PersonName("Ada", "Lovelace"), allergies = listOf("MILK"))),
                pledgeName = "family",
                activities = listOf(Activity.CEREMONY),
                registeredAt = Instant.parse("2019-02-14T12:00:00Z"),
            ),
        )

        val loaded = registrations.findByEmail("ada@example.com")

        assertThat(loaded).isNotNull
        assertThat(loaded!!.guests.single().allergies).containsExactly("MILK")
        assertThat(loaded.activities).containsExactly(Activity.CEREMONY)
        assertThat(loaded.registeredAt).isEqualTo(Instant.parse("2019-02-14T12:00:00Z"))
    }

    companion object {
        @Container
        @JvmStatic
        private val mongo = MongoDBContainer("mongo:8.0")

        @JvmStatic
        @DynamicPropertySource
        fun mongoProperties(registry: DynamicPropertyRegistry) {
            if (!DockerClientFactory.instance().isDockerAvailable) return

            // The persistence type itself cannot be set here: `spring.profiles.include` reads it
            // while config data is processed, which is before dynamic properties are registered.
            // Hence the static `properties` on @SpringBootTest above.
            registry.add("spring.mongodb.uri", mongo::getReplicaSetUrl)
        }
    }
}
