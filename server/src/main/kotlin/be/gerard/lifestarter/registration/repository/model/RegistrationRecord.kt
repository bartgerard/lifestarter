package be.gerard.lifestarter.registration.repository.model

import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.ContactMethod
import be.gerard.lifestarter.registration.domain.Diet
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * Mirrors the historical `registration` collection.
 *
 * Field names — including the legacy `registrationDateTime` — are preserved so documents written by
 * the original Java application keep deserialising.
 */
@Document(collection = "registration")
data class RegistrationRecord(
    @Id val email: String,
    val guests: List<GuestRecord> = emptyList(),
    val contactOptions: List<ContactOptionRecord> = emptyList(),
    val pledgeName: String? = null,
    val activities: List<Activity> = emptyList(),
    val registrationDateTime: Instant,
)

data class GuestRecord(
    val firstName: String,
    val lastName: String,
    val role: String? = null,
    val diet: Diet? = null,
    val allergies: List<String> = emptyList(),
    val comment: String? = null,
)

data class ContactOptionRecord(
    val email: String? = null,
    val address: String? = null,
    val zipCode: String? = null,
    val city: String? = null,
    val countryIso3: String? = null,
    val phoneNumber: String? = null,
    val contactMethod: ContactMethod = ContactMethod.NONE,
)
