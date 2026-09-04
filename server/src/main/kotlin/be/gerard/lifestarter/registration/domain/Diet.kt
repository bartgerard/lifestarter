package be.gerard.lifestarter.registration.domain

/** Dietary preference of a guest. */
enum class Diet {
    /** Eats everything. */
    MEAT_EATER,

    /** Does not eat meat, but does eat fish. */
    PESCATARIAN,

    /** Does not eat meat or fish. */
    VEGETARIAN,

    /** Does not eat or use any animal product. */
    VEGAN,

    /** Eats only fruit. */
    FRUITARIAN,

    /** Mostly vegetarian, occasionally not. */
    FLEXITARIAN,
}
