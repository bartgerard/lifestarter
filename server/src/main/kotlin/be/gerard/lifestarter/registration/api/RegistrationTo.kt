package be.gerard.lifestarter.registration.api

import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.ContactMethod
import be.gerard.lifestarter.registration.domain.Diet
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.Instant

/** Request body for filing a new RSVP. */
data class NewRegistrationTo(
    @field:NotBlank
    @field:Email
    @field:Size(max = 320)
    val email: String?,

    @field:NotEmpty
    @field:Size(max = 50)
    @field:Valid
    val guests: List<GuestTo> = emptyList(),

    @field:Size(max = 10)
    @field:Valid
    val contactOptions: List<ContactOptionTo> = emptyList(),

    @field:Size(max = 100)
    val pledgeName: String? = null,

    val activities: List<Activity> = emptyList(),
)

data class GuestTo(
    @field:NotBlank
    @field:Size(max = 100)
    val firstName: String?,

    @field:NotBlank
    @field:Size(max = 100)
    val lastName: String?,

    @field:Size(max = 100)
    val role: String? = null,

    val diet: Diet? = null,

    @field:Size(max = 50)
    val allergies: List<String> = emptyList(),

    @field:Size(max = 1000)
    val comment: String? = null,
)

data class ContactOptionTo(
    @field:Size(max = 320)
    val email: String? = null,

    @field:Size(max = 200)
    val address: String? = null,

    @field:Size(max = 20)
    val zipCode: String? = null,

    @field:Size(max = 100)
    val city: String? = null,

    @field:Size(max = 3)
    val countryIso3: String? = null,

    @field:Size(max = 50)
    val phoneNumber: String? = null,

    val contactMethod: ContactMethod = ContactMethod.NONE,
)

/** Response body describing a stored RSVP. */
data class RegistrationTo(
    val email: String,
    val guests: List<GuestTo>,
    val contactOptions: List<ContactOptionTo>,
    val pledgeName: String?,
    val activities: List<Activity>,
    val registeredAt: Instant,
)

/** Aggregated RSVP counters. */
data class RegistrationStatisticsTo(
    val totalRegistrations: Int,
    val totalGuests: Int,
    val dinnerGuests: Int,
    val guestsPerPledge: Map<String, Int>,
)
