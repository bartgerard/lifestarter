package be.gerard.lifestarter.pledge.domain

import java.math.BigDecimal

/**
 * A tier guests can sign up for, describing what they get and what it costs.
 *
 * Purely descriptive: how many guests actually picked it is a [PledgeOffer] concern.
 */
data class Pledge(
    val name: String,
    val orderId: Int,
    val price: BigDecimal = BigDecimal.ZERO,
    val description: String = "",
    val contents: List<String> = emptyList(),
    val limit: Int = 0,
    val available: Boolean = true,
) {
    init {
        require(name.isNotBlank()) { "pledge.name must not be blank" }
        require(price >= BigDecimal.ZERO) { "pledge.price must not be negative" }
        require(limit >= 0) { "pledge.limit must not be negative" }
    }

    /** `true` when this pledge caps the number of guests. */
    val isLimited: Boolean
        get() = limit > 0
}
