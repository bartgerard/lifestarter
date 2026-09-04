package be.gerard.lifestarter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.client.RestTestClient

/**
 * Exercises the whole hexagon over HTTP against a real (throwaway) SQLite database.
 *
 * This is the test that catches a broken persistence switch, a missing Flyway migration or a
 * mis-mapped DTO — the things unit tests with mocked ports cannot see.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegistrationApiIntegrationTest(
    @LocalServerPort private val port: Int,
) {

    private lateinit var client: RestTestClient

    @BeforeEach
    fun createClient() {
        client = RestTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    @Test
    fun `serves the reference data seeded through the ports`() {
        client.get().uri("/api/allergies").exchange()
            .expectStatus().isOk()
            .expectBody(Array<String>::class.java)
            .value { assertThat(it).contains("MILK", "PEANUTS") }

        client.get().uri("/api/diets").exchange()
            .expectStatus().isOk()
            .expectBody(Array<String>::class.java)
            .value { assertThat(it).contains("VEGETARIAN", "VEGAN") }

        client.get().uri("/api/pledges").exchange()
            .expectStatus().isOk()
            .expectBody(Array<Any>::class.java)
            .value { assertThat(it).hasSize(8) }
    }

    @Test
    fun `accepts an RSVP and reflects it in the statistics`() {
        postRsvp()
            .expectStatus().isCreated()
            .expectHeader().valueMatches(HttpHeaders.LOCATION, ".*/api/registrations/ada%40example\\.com")
            .expectBody(String::class.java)
            .value { assertThat(it).contains("\"email\":\"ada@example.com\"") }

        client.get().uri("/api/registrations/statistics")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String::class.java)
            .value {
                assertThat(it)
                    .contains("\"totalRegistrations\":1")
                    .contains("\"totalGuests\":2")
                    .contains("\"dinnerGuests\":2")
                    .contains("\"family\":2")
            }
    }

    @Test
    fun `rejects an invalid RSVP with a problem document`() {
        client.post().uri("/api/registrations")
            .contentType(MediaType.APPLICATION_JSON)
            .body("""{"email":"not-an-email","guests":[]}""")
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
    }

    @Test
    fun `guards the export but leaves the aggregate counters public`() {
        client.get().uri("/api/exports/registrations").exchange()
            .expectStatus().isUnauthorized()

        client.get().uri("/api/registrations/statistics").exchange()
            .expectStatus().isOk()
    }

    @Test
    fun `streams the registration export as a spreadsheet`() {
        postRsvp().expectStatus().isCreated()

        val body = client.get().uri("/api/exports/registrations")
            .headers(::asAdmin)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value(HttpHeaders.CONTENT_DISPOSITION) {
                assertThat(it).contains(".xlsx")
            }
            .expectBody(ByteArray::class.java)
            .returnResult()
            .responseBody

        // "PK" — every .xlsx is a zip container.
        assertThat(body).isNotNull
        assertThat(body!!.take(2)).containsExactly(0x50, 0x4B)
    }

    @Test
    fun `falls back to pledge defaults when nobody is on the bouncer list`() {
        client.get()
            .uri("/api/access/activities?pledge=family&first-first-name=John&first-last-name=Doe")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Array<String>::class.java)
            .value { assertThat(it).containsExactly("CEREMONY", "DINNER", "PARTY") }
    }

    @Test
    fun `reports no open wave once every deadline has passed`() {
        client.get().uri("/api/waves/current").exchange()
            .expectStatus().isNotFound()
    }

    private fun postRsvp() = client.post().uri("/api/registrations")
        .contentType(MediaType.APPLICATION_JSON)
        .body(RSVP)
        .exchange()

    private fun asAdmin(headers: HttpHeaders) =
        headers.setBasicAuth(SqliteTestDatabase.ADMIN_USERNAME, SqliteTestDatabase.ADMIN_PASSWORD)

    companion object {
        private const val RSVP = """
            {
              "email": "ada@example.com",
              "pledgeName": "family",
              "activities": ["CEREMONY", "DINNER"],
              "guests": [
                {"firstName": "Ada", "lastName": "Lovelace", "diet": "VEGETARIAN", "allergies": ["MILK"]},
                {"firstName": "Charles", "lastName": "Babbage"}
              ],
              "contactOptions": [
                {"email": "ada@example.com", "city": "London", "countryIso3": "GBR", "contactMethod": "EMAIL"}
              ]
            }
        """

        @JvmStatic
        @DynamicPropertySource
        fun sqlite(registry: DynamicPropertyRegistry) = registry.useThrowawaySqlite("api")
    }
}
