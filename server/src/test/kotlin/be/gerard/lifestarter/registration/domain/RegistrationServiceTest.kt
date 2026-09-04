package be.gerard.lifestarter.registration.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class RegistrationServiceTest {

    private val registrations = mockk<RegistrationRepository>()
    private val eventPublisher = mockk<RegistrationEventPublisher>(relaxed = true)
    private val clock = Clock.fixed(Instant.parse("2019-02-14T12:00:00Z"), ZoneOffset.UTC)
    private val service = RegistrationService(registrations, eventPublisher, clock)

    @Test
    fun `stamps the registration with the current time and stores it`() {
        every { registrations.save(any()) } answers { firstArg() }

        val registered = service.register(newRegistration())

        assertThat(registered.registeredAt).isEqualTo(Instant.parse("2019-02-14T12:00:00Z"))
        assertThat(registered.email).isEqualTo("ada@example.com")
    }

    @Test
    fun `trims the email and drops a blank pledge`() {
        every { registrations.save(any()) } answers { firstArg() }

        val registered = service.register(
            newRegistration().copy(email = "  ada@example.com  ", pledgeName = "   "),
        )

        assertThat(registered.email).isEqualTo("ada@example.com")
        assertThat(registered.pledgeName).isNull()
    }

    @Test
    fun `deduplicates and orders the activities`() {
        every { registrations.save(any()) } answers { firstArg() }

        val registered = service.register(
            newRegistration().copy(
                activities = listOf(Activity.PARTY, Activity.CEREMONY, Activity.PARTY),
            ),
        )

        assertThat(registered.activities).containsExactly(Activity.CEREMONY, Activity.PARTY)
    }

    @Test
    fun `publishes the stored registration, not the incoming one`() {
        val stored = registration().copy(email = "stored@example.com")
        every { registrations.save(any()) } returns stored

        service.register(newRegistration())

        val event = slot<RegistrationAdded>()
        verify { eventPublisher.publish(capture(event)) }
        assertThat(event.captured.registration).isEqualTo(stored)
    }

    @Test
    fun `refuses a registration without guests`() {
        assertThatThrownBy { service.register(newRegistration().copy(guests = emptyList())) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("at least one guest")
    }

    private fun newRegistration() = NewRegistration(
        email = "ada@example.com",
        guests = listOf(Guest(PersonName("Ada", "Lovelace"))),
        pledgeName = "family",
    )

    private fun registration() = Registration(
        email = "ada@example.com",
        guests = listOf(Guest(PersonName("Ada", "Lovelace"))),
        registeredAt = Instant.parse("2019-02-14T12:00:00Z"),
    )
}

class RegistrationStatisticsTest {

    @Test
    fun `counts guests overall, per pledge and for the dinner`() {
        val statistics = RegistrationStatistics.of(
            listOf(
                registration("family@example.com", guests = 2, pledge = "family", Activity.DINNER),
                registration("friends@example.com", guests = 3, pledge = "friends"),
                registration("more@example.com", guests = 1, pledge = "family", Activity.DINNER),
            ),
        )

        assertThat(statistics.totalRegistrations).isEqualTo(3)
        assertThat(statistics.totalGuests).isEqualTo(6)
        assertThat(statistics.dinnerGuests).isEqualTo(3)
        assertThat(statistics.guestsPerPledge).containsExactlyInAnyOrderEntriesOf(
            mapOf("family" to 3, "friends" to 3),
        )
    }

    @Test
    fun `ignores registrations without a pledge`() {
        val statistics = RegistrationStatistics.of(
            listOf(registration("none@example.com", guests = 2, pledge = null)),
        )

        assertThat(statistics.guestsPerPledge).isEmpty()
        assertThat(statistics.totalGuests).isEqualTo(2)
    }

    private fun registration(
        email: String,
        guests: Int,
        pledge: String?,
        vararg activities: Activity,
    ) = Registration(
        email = email,
        guests = (1..guests).map { Guest(PersonName("Guest$it", "Doe")) },
        pledgeName = pledge,
        activities = activities.toList(),
        registeredAt = Instant.parse("2019-02-14T12:00:00Z"),
    )
}
