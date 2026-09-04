package be.gerard.lifestarter.pledge.domain

/** A pledge together with how many guests already subscribed to it. */
data class PledgeOffer(
    val pledge: Pledge,
    val subscribedGuests: Int,
) {
    init {
        require(subscribedGuests >= 0) { "subscribedGuests must not be negative" }
    }

    /** A limited pledge stops being offered once it is full. */
    val isSelectable: Boolean
        get() = pledge.available && (!pledge.isLimited || subscribedGuests < pledge.limit)
}
