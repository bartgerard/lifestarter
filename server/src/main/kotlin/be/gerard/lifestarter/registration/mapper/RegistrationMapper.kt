package be.gerard.lifestarter.registration.mapper

import be.gerard.lifestarter.registration.api.ContactOptionTo
import be.gerard.lifestarter.registration.api.GuestTo
import be.gerard.lifestarter.registration.api.NewRegistrationTo
import be.gerard.lifestarter.registration.api.RegistrationStatisticsTo
import be.gerard.lifestarter.registration.api.RegistrationTo
import be.gerard.lifestarter.registration.domain.ContactOption
import be.gerard.lifestarter.registration.domain.Guest
import be.gerard.lifestarter.registration.domain.NewRegistration
import be.gerard.lifestarter.registration.domain.PersonName
import be.gerard.lifestarter.registration.domain.Registration
import be.gerard.lifestarter.registration.domain.RegistrationStatistics
import org.springframework.stereotype.Component

/**
 * Translates between the REST transfer objects and the domain.
 *
 * The transfer objects allow nulls because Bean Validation reports them as `400`s; by the time
 * mapping happens the request is valid, so `requireNotNull` documents that contract.
 */
@Component
class RegistrationMapper {

    fun toCommand(request: NewRegistrationTo): NewRegistration = NewRegistration(
        email = requireNotNull(request.email) { "email is required" },
        guests = request.guests.map(::toGuest),
        contactOptions = request.contactOptions.map(::toContactOption),
        pledgeName = request.pledgeName,
        activities = request.activities,
    )

    fun toTo(registration: Registration): RegistrationTo = RegistrationTo(
        email = registration.email,
        guests = registration.guests.map(::toTo),
        contactOptions = registration.contactOptions.map(::toTo),
        pledgeName = registration.pledgeName,
        activities = registration.activities,
        registeredAt = registration.registeredAt,
    )

    fun toTo(statistics: RegistrationStatistics): RegistrationStatisticsTo = RegistrationStatisticsTo(
        totalRegistrations = statistics.totalRegistrations,
        totalGuests = statistics.totalGuests,
        dinnerGuests = statistics.dinnerGuests,
        guestsPerPledge = statistics.guestsPerPledge,
    )

    private fun toGuest(request: GuestTo): Guest = Guest(
        name = PersonName(
            firstName = requireNotNull(request.firstName) { "guest.firstName is required" },
            lastName = requireNotNull(request.lastName) { "guest.lastName is required" },
        ),
        role = request.role,
        diet = request.diet,
        allergies = request.allergies,
        comment = request.comment,
    )

    private fun toContactOption(request: ContactOptionTo): ContactOption = ContactOption(
        email = request.email,
        address = request.address,
        zipCode = request.zipCode,
        city = request.city,
        countryIso3 = request.countryIso3,
        phoneNumber = request.phoneNumber,
        contactMethod = request.contactMethod,
    )

    private fun toTo(guest: Guest): GuestTo = GuestTo(
        firstName = guest.firstName,
        lastName = guest.lastName,
        role = guest.role,
        diet = guest.diet,
        allergies = guest.allergies,
        comment = guest.comment,
    )

    private fun toTo(option: ContactOption): ContactOptionTo = ContactOptionTo(
        email = option.email,
        address = option.address,
        zipCode = option.zipCode,
        city = option.city,
        countryIso3 = option.countryIso3,
        phoneNumber = option.phoneNumber,
        contactMethod = option.contactMethod,
    )
}
