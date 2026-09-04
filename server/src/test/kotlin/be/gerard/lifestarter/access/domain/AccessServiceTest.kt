package be.gerard.lifestarter.access.domain

import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.PersonName
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AccessServiceTest {

    private val vips = mockk<VipRepository>()
    private val bouncers = mockk<BouncerRepository>()
    private val service = AccessService(vips, bouncers)

    @Test
    fun `looks a VIP up case-insensitively`() {
        every { vips.findByName(PersonName("ada", "lovelace")) } returns
            Vip(PersonName("ada", "lovelace"), listOf("WITNESS"))

        assertThat(service.findRoles(PersonName("  ADA ", "Lovelace"))).containsExactly("WITNESS")
    }

    @Test
    fun `returns no roles for an unknown guest`() {
        every { vips.findByName(any()) } returns null

        assertThat(service.findRoles(PersonName("John", "Doe"))).isEmpty()
    }

    @Test
    fun `a bouncer entry overrides the pledge defaults`() {
        every { bouncers.findByName(PersonName("ada", "lovelace")) } returns
            Bouncer(PersonName("ada", "lovelace"), listOf(Activity.DINNER))

        val activities = service.findActivities("friends", listOf(PersonName("Ada", "Lovelace")))

        assertThat(activities).containsExactly(Activity.DINNER)
    }

    @Test
    fun `merges the activities of everyone in the party`() {
        every { bouncers.findByName(PersonName("ada", "lovelace")) } returns
            Bouncer(PersonName("ada", "lovelace"), listOf(Activity.PARTY))
        every { bouncers.findByName(PersonName("charles", "babbage")) } returns
            Bouncer(PersonName("charles", "babbage"), listOf(Activity.CEREMONY, Activity.PARTY))

        val activities = service.findActivities(
            "friends",
            listOf(PersonName("Ada", "Lovelace"), PersonName("Charles", "Babbage")),
        )

        assertThat(activities).containsExactly(Activity.CEREMONY, Activity.PARTY)
    }

    @Test
    fun `family gets dinner, plain friends do not`() {
        every { bouncers.findByName(any()) } returns null

        val party = listOf(PersonName("John", "Doe"))

        assertThat(service.findActivities("family", party))
            .containsExactly(Activity.CEREMONY, Activity.DINNER, Activity.PARTY)
        assertThat(service.findActivities("friends", party))
            .containsExactly(Activity.CEREMONY, Activity.PARTY)
        assertThat(service.findActivities(null, party))
            .containsExactly(Activity.CEREMONY, Activity.PARTY)
    }
}
