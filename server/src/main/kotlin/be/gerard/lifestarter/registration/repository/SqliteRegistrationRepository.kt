package be.gerard.lifestarter.registration.repository

import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.ContactMethod
import be.gerard.lifestarter.registration.domain.ContactOption
import be.gerard.lifestarter.registration.domain.Diet
import be.gerard.lifestarter.registration.domain.Guest
import be.gerard.lifestarter.registration.domain.PersonName
import be.gerard.lifestarter.registration.domain.Registration
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Stores the registration aggregate across its normalised tables.
 *
 * Reads are assembled with one query per child table rather than a join, which keeps the row
 * mapping trivial and avoids the cartesian blow-up between guests, contact options and activities.
 *
 * Class-level `@Transactional` also makes the Kotlin all-open plugin unseal the class, which Spring
 * needs in order to proxy it. Reads default to `readOnly`.
 */
@Transactional(readOnly = true)
class SqliteRegistrationRepository(
    private val jdbc: JdbcClient,
) : RegistrationRepository {

    override fun findAll(): List<Registration> = assemble(
        jdbc.sql("SELECT email, pledge_name, registered_at FROM registration ORDER BY registered_at")
            .query(::toHeader)
            .list()
    )

    override fun findByEmail(email: String): Registration? = assemble(
        jdbc.sql("SELECT email, pledge_name, registered_at FROM registration WHERE email = :email")
            .param("email", email)
            .query(::toHeader)
            .list()
    ).firstOrNull()

    @Transactional
    override fun save(registration: Registration): Registration {
        // Re-filing an RSVP replaces the previous one; the cascades clear the children for us.
        jdbc.sql("DELETE FROM registration WHERE email = :email")
            .param("email", registration.email)
            .update()

        jdbc.sql(
            """
            INSERT INTO registration (email, pledge_name, registered_at)
            VALUES (:email, :pledgeName, :registeredAt)
            """
        )
            .param("email", registration.email)
            .param("pledgeName", registration.pledgeName)
            .param("registeredAt", registration.registeredAt.toString())
            .update()

        registration.activities.forEach { activity ->
            jdbc.sql(
                """
                INSERT OR IGNORE INTO registration_activity (registration_email, activity)
                VALUES (:email, :activity)
                """
            )
                .param("email", registration.email)
                .param("activity", activity.name)
                .update()
        }

        registration.guests.forEachIndexed { position, guest -> insertGuest(registration.email, position, guest) }

        registration.contactOptions.forEachIndexed { position, option ->
            insertContactOption(registration.email, position, option)
        }

        return registration
    }

    private fun insertGuest(email: String, position: Int, guest: Guest) {
        jdbc.sql(
            """
            INSERT INTO registration_guest
                (registration_email, position, first_name, last_name, role, diet, comment)
            VALUES (:email, :position, :firstName, :lastName, :role, :diet, :comment)
            """
        )
            .param("email", email)
            .param("position", position)
            .param("firstName", guest.firstName)
            .param("lastName", guest.lastName)
            .param("role", guest.role)
            .param("diet", guest.diet?.name)
            .param("comment", guest.comment)
            .update()

        guest.allergies.distinct().forEach { allergy ->
            jdbc.sql(
                """
                INSERT OR IGNORE INTO registration_guest_allergy
                    (registration_email, guest_position, allergy_id)
                VALUES (:email, :position, :allergy)
                """
            )
                .param("email", email)
                .param("position", position)
                .param("allergy", allergy)
                .update()
        }
    }

    private fun insertContactOption(email: String, position: Int, option: ContactOption) {
        jdbc.sql(
            """
            INSERT INTO registration_contact_option
                (registration_email, position, email, address, zip_code, city, country_iso3,
                 phone_number, contact_method)
            VALUES (:registrationEmail, :position, :email, :address, :zipCode, :city, :countryIso3,
                    :phoneNumber, :contactMethod)
            """
        )
            .param("registrationEmail", email)
            .param("position", position)
            .param("email", option.email)
            .param("address", option.address)
            .param("zipCode", option.zipCode)
            .param("city", option.city)
            .param("countryIso3", option.countryIso3)
            .param("phoneNumber", option.phoneNumber)
            .param("contactMethod", option.contactMethod.name)
            .update()
    }

    private fun assemble(headers: List<RegistrationHeader>): List<Registration> {
        if (headers.isEmpty()) return emptyList()

        val emails = headers.map(RegistrationHeader::email)
        val activities = findActivities(emails)
        val guests = findGuests(emails)
        val contactOptions = findContactOptions(emails)

        return headers.map { header ->
            Registration(
                email = header.email,
                guests = guests[header.email].orEmpty(),
                contactOptions = contactOptions[header.email].orEmpty(),
                pledgeName = header.pledgeName,
                activities = activities[header.email].orEmpty(),
                registeredAt = header.registeredAt,
            )
        }
    }

    private fun findActivities(emails: List<String>): Map<String, List<Activity>> = jdbc
        .sql("SELECT registration_email, activity FROM registration_activity WHERE registration_email IN (:emails)")
        .param("emails", emails)
        .query { rs, _ -> rs.getString("registration_email") to Activity.valueOf(rs.getString("activity")) }
        .list()
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, values) -> values.sorted() }

    private fun findContactOptions(emails: List<String>): Map<String, List<ContactOption>> = jdbc
        .sql(
            """
            SELECT registration_email, email, address, zip_code, city, country_iso3, phone_number,
                   contact_method
            FROM registration_contact_option
            WHERE registration_email IN (:emails)
            ORDER BY registration_email, position
            """
        )
        .param("emails", emails)
        .query { rs, _ ->
            rs.getString("registration_email") to ContactOption(
                email = rs.getString("email"),
                address = rs.getString("address"),
                zipCode = rs.getString("zip_code"),
                city = rs.getString("city"),
                countryIso3 = rs.getString("country_iso3"),
                phoneNumber = rs.getString("phone_number"),
                contactMethod = ContactMethod.valueOf(rs.getString("contact_method")),
            )
        }
        .list()
        .groupBy({ it.first }, { it.second })

    private fun findGuests(emails: List<String>): Map<String, List<Guest>> {
        val allergies = jdbc
            .sql(
                """
                SELECT registration_email, guest_position, allergy_id
                FROM registration_guest_allergy
                WHERE registration_email IN (:emails)
                ORDER BY registration_email, guest_position, allergy_id
                """
            )
            .param("emails", emails)
            .query { rs, _ ->
                GuestKey(rs.getString("registration_email"), rs.getInt("guest_position")) to
                    rs.getString("allergy_id")
            }
            .list()
            .groupBy({ it.first }, { it.second })

        return jdbc
            .sql(
                """
                SELECT registration_email, position, first_name, last_name, role, diet, comment
                FROM registration_guest
                WHERE registration_email IN (:emails)
                ORDER BY registration_email, position
                """
            )
            .param("emails", emails)
            .query { rs, _ ->
                val email = rs.getString("registration_email")
                val key = GuestKey(email, rs.getInt("position"))

                email to Guest(
                    name = PersonName(rs.getString("first_name"), rs.getString("last_name")),
                    role = rs.getString("role"),
                    diet = rs.getString("diet")?.let(Diet::valueOf),
                    allergies = allergies[key].orEmpty(),
                    comment = rs.getString("comment"),
                )
            }
            .list()
            .groupBy({ it.first }, { it.second })
    }

    private data class GuestKey(val email: String, val position: Int)

    private data class RegistrationHeader(
        val email: String,
        val pledgeName: String?,
        val registeredAt: Instant,
    )

    private companion object {
        fun toHeader(rs: java.sql.ResultSet, rowNum: Int): RegistrationHeader = RegistrationHeader(
            email = rs.getString("email"),
            pledgeName = rs.getString("pledge_name"),
            registeredAt = Instant.parse(rs.getString("registered_at")),
        )
    }
}
