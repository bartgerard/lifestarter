package be.gerard.lifestarter.pledge.domain

/** Outbound port for the pledge catalogue. */
interface PledgeRepository {
    fun findAll(): List<Pledge>

    fun findByName(name: String): Pledge?

    /** Inserts the pledges that do not exist yet; already-known ones are left untouched. */
    fun saveAll(pledges: List<Pledge>)
}

/**
 * Outbound port telling the pledge slice how many guests each pledge already attracted.
 *
 * Keeps [PledgeService] independent of the registration slice.
 */
fun interface PledgeSubscriptionCounter {
    fun countGuestsPerPledge(): Map<String, Int>
}
