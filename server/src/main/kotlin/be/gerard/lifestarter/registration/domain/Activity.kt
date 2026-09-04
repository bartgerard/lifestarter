package be.gerard.lifestarter.registration.domain

/**
 * A part of the wedding day a guest can be invited to.
 *
 * Declaration order is significant: it defines the chronological order of the day and is relied
 * upon when activities are presented to the outside world.
 */
enum class Activity {
    OFFICIAL,
    PHOTO_SHOOT,
    CEREMONY,
    RECEPTION,
    DINNER,
    PARTY,
}
