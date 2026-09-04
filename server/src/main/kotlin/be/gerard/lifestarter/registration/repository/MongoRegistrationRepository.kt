package be.gerard.lifestarter.registration.repository

import be.gerard.lifestarter.registration.domain.ContactOption
import be.gerard.lifestarter.registration.domain.Guest
import be.gerard.lifestarter.registration.domain.PersonName
import be.gerard.lifestarter.registration.domain.Registration
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import be.gerard.lifestarter.registration.repository.model.ContactOptionRecord
import be.gerard.lifestarter.registration.repository.model.GuestRecord
import be.gerard.lifestarter.registration.repository.model.RegistrationRecord
import org.springframework.data.mongodb.core.MongoOperations

class MongoRegistrationRepository(
    private val mongo: MongoOperations,
) : RegistrationRepository {

    override fun findAll(): List<Registration> = mongo.findAll(RegistrationRecord::class.java)
        .map(::toDomain)

    override fun findByEmail(email: String): Registration? =
        mongo.findById(email, RegistrationRecord::class.java)?.let(::toDomain)

    override fun save(registration: Registration): Registration {
        mongo.save(toRecord(registration))
        return registration
    }

    private fun toDomain(record: RegistrationRecord): Registration = Registration(
        email = record.email,
        guests = record.guests.map { guest ->
            Guest(
                name = PersonName(guest.firstName, guest.lastName),
                role = guest.role,
                diet = guest.diet,
                allergies = guest.allergies,
                comment = guest.comment,
            )
        },
        contactOptions = record.contactOptions.map { option ->
            ContactOption(
                email = option.email,
                address = option.address,
                zipCode = option.zipCode,
                city = option.city,
                countryIso3 = option.countryIso3,
                phoneNumber = option.phoneNumber,
                contactMethod = option.contactMethod,
            )
        },
        pledgeName = record.pledgeName,
        activities = record.activities.sorted(),
        registeredAt = record.registrationDateTime,
    )

    private fun toRecord(registration: Registration): RegistrationRecord = RegistrationRecord(
        email = registration.email,
        guests = registration.guests.map { guest ->
            GuestRecord(
                firstName = guest.firstName,
                lastName = guest.lastName,
                role = guest.role,
                diet = guest.diet,
                allergies = guest.allergies,
                comment = guest.comment,
            )
        },
        contactOptions = registration.contactOptions.map { option ->
            ContactOptionRecord(
                email = option.email,
                address = option.address,
                zipCode = option.zipCode,
                city = option.city,
                countryIso3 = option.countryIso3,
                phoneNumber = option.phoneNumber,
                contactMethod = option.contactMethod,
            )
        },
        pledgeName = registration.pledgeName,
        activities = registration.activities,
        registrationDateTime = registration.registeredAt,
    )
}
