package be.gerard.lifestarter.access.domain

import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.PersonName

/**
 * Answers "what is this party allowed to attend, and what extra roles do they have?".
 *
 * Bouncers override the defaults: as soon as any member of the party is on the bouncer list, the
 * union of their activities wins. Otherwise the pledge decides.
 */
class AccessService(
    private val vips: VipRepository,
    private val bouncers: BouncerRepository,
) {
    fun findRoles(name: PersonName): List<String> =
        vips.findByName(name.normalised())?.roles.orEmpty()

    fun findActivities(pledgeName: String?, names: List<PersonName>): List<Activity> {
        val granted = names.asSequence()
            .map(PersonName::normalised)
            .mapNotNull(bouncers::findByName)
            .flatMap { it.activities.asSequence() }
            .distinct()
            .sorted()
            .toList()

        return granted.ifEmpty { defaultActivitiesFor(pledgeName) }
    }

    private fun defaultActivitiesFor(pledgeName: String?): List<Activity> =
        if (pledgeName in PLEDGES_INCLUDING_DINNER) {
            listOf(Activity.CEREMONY, Activity.DINNER, Activity.PARTY)
        } else {
            listOf(Activity.CEREMONY, Activity.PARTY)
        }

    companion object {
        private val PLEDGES_INCLUDING_DINNER = setOf("family", "early bird friends")
    }
}
