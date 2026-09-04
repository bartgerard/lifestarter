package be.gerard.lifestarter.repository

import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.ContactMethod
import be.gerard.lifestarter.registration.domain.ContactOption
import be.gerard.lifestarter.registration.domain.Diet
import be.gerard.lifestarter.registration.domain.Guest
import be.gerard.lifestarter.registration.domain.PersonName
import be.gerard.lifestarter.registration.domain.Registration
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import be.gerard.lifestarter.useThrowawaySqlite
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.time.Instant

/**
 * Round-trips the aggregate through the normalised SQLite schema.
 *
 * The interesting part is not that a row comes back, but that the children — guests, their
 * allergies, contact options and activities — survive the split across five tables intact.
 */
@SpringBootTest
class SqliteRegistrationRepositoryTest(
    @Autowired private val registrations: RegistrationRepository,
) {

    @Test
    fun `stores and reassembles the full aggregate`() {
        registrations.save(registration("ada@example.com"))

        val loaded = registrations.findByEmail("ada@example.com")

        assertThat(loaded).isNotNull
        assertThat(loaded!!.guests).hasSize(2)
        assertThat(loaded.guests.first().name).isEqualTo(PersonName("Ada", "Lovelace"))
        assertThat(loaded.guests.first().allergies).containsExactly("MILK", "SOY")
        assertThat(loaded.guests.first().diet).isEqualTo(Diet.VEGETARIAN)
        assertThat(loaded.guests[1].allergies).isEmpty()
        assertThat(loaded.contactOptions).hasSize(1)
        assertThat(loaded.contactOptions.first().contactMethod).isEqualTo(ContactMethod.EMAIL)
        assertThat(loaded.activities).containsExactly(Activity.CEREMONY, Activity.DINNER)
        assertThat(loaded.pledgeName).isEqualTo("family")
    }

    @Test
    fun `re-filing an RSVP replaces the previous one instead of duplicating it`() {
        registrations.save(registration("charles@example.com"))
        registrations.save(
            registration("charles@example.com").copy(
                guests = listOf(Guest(PersonName("Charles", "Babbage"))),
                pledgeName = "friends",
            ),
        )

        val matching = registrations.findAll().filter { it.email == "charles@example.com" }

        assertThat(matching).hasSize(1)
        assertThat(matching.single().guests).hasSize(1)
        assertThat(matching.single().pledgeName).isEqualTo("friends")
    }

    @Test
    fun `returns null for an unknown email`() {
        assertThat(registrations.findByEmail("nobody@example.com")).isNull()
    }

    private fun registration(email: String) = Registration(
        email = email,
        guests = listOf(
            Guest(
                name = PersonName("Ada", "Lovelace"),
                role = "WITNESS",
                diet = Diet.VEGETARIAN,
                allergies = listOf("MILK", "SOY"),
                comment = "window seat",
            ),
            Guest(PersonName("Charles", "Babbage")),
        ),
        contactOptions = listOf(
            ContactOption(
                email = email,
                address = "1 Analytical Way",
                zipCode = "SW1",
                city = "London",
                countryIso3 = "GBR",
                phoneNumber = "+44 20 0000 0000",
                contactMethod = ContactMethod.EMAIL,
            ),
        ),
        pledgeName = "family",
        activities = listOf(Activity.CEREMONY, Activity.DINNER),
        registeredAt = Instant.parse("2019-02-14T12:00:00Z"),
    )

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun sqlite(registry: DynamicPropertyRegistry) = registry.useThrowawaySqlite("registrations")
    }
}
