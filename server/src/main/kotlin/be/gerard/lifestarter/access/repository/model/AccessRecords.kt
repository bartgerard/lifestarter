package be.gerard.lifestarter.access.repository.model

import be.gerard.lifestarter.registration.domain.Activity
import org.springframework.data.mongodb.core.mapping.Document

/** Mirrors the historical `vip` collection. */
@Document(collection = "vip")
data class VipRecord(
    val firstName: String,
    val lastName: String,
    val roles: List<String> = emptyList(),
)

/** Mirrors the historical `bouncer` collection. */
@Document(collection = "bouncer")
data class BouncerRecord(
    val firstName: String,
    val lastName: String,
    val activities: List<Activity> = emptyList(),
)
