package be.gerard.lifestarter.access.domain

import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.PersonName

/** Someone who gets extra roles in the ceremony, e.g. a witness or a ring bearer. */
data class Vip(
    val name: PersonName,
    val roles: List<String> = emptyList(),
)

/** Someone whose attendance is explicitly restricted to a fixed set of activities. */
data class Bouncer(
    val name: PersonName,
    val activities: List<Activity> = emptyList(),
)

/** Outbound port for the VIP list. */
interface VipRepository {
    fun findByName(name: PersonName): Vip?

    fun findAll(): List<Vip>

    fun saveAll(vips: List<Vip>)
}

/** Outbound port for the bouncer list. */
interface BouncerRepository {
    fun findByName(name: PersonName): Bouncer?

    fun findAll(): List<Bouncer>

    fun saveAll(bouncers: List<Bouncer>)
}
