package be.gerard.lifestarter.pledge.domain

import java.math.BigDecimal

class PledgeService(
    private val pledges: PledgeRepository,
    private val subscriptions: PledgeSubscriptionCounter,
) {
    /** Every pledge, in presentation order, enriched with its current subscription count. */
    fun findAllOffers(): List<PledgeOffer> {
        val guestsPerPledge = subscriptions.countGuestsPerPledge()

        return pledges.findAll()
            .sortedBy(Pledge::orderId)
            .map { pledge -> PledgeOffer(pledge, guestsPerPledge[pledge.name] ?: 0) }
    }

    companion object {
        /** The pledge catalogue this wedding ships with. */
        val DEFAULTS: List<Pledge> = listOf(
            Pledge(
                name = "early bird friends",
                orderId = 1,
                description = "Toegang tot alle festiviteiten.",
                contents = listOf("2x Toegang tot alle festiviteiten"),
            ),
            Pledge(
                name = "friends",
                orderId = 2,
                description = "Toegang tot bijna alle festiviteiten. Helaas kunnen we niet iedereen " +
                    "uitnodigen op de feestmaaltijd, maar wees welkom op het aansluitende dessertenbuffet!",
                contents = listOf("2x Toegang tot bijna alle festiviteiten"),
            ),
            Pledge(
                name = "family",
                orderId = 3,
                description = "Toegang tot alle festiviteiten.",
                contents = listOf("2x Toegang tot alle festiviteiten"),
            ),
            Pledge(
                name = "ring bearer",
                orderId = 4,
                description = "Ringbear :-)",
                contents = listOf(
                    "2x Toegang tot alle festiviteiten",
                    "1x Toegang tot de ceremonie alwaar je een zeer specifieke taak krijgt toegewezen",
                ),
            ),
            Pledge(
                name = "vip",
                orderId = 5,
                price = BigDecimal.valueOf(250),
                description = "Omvat één geprinte versie van het trouwalbum.",
                contents = listOf(
                    "2x Toegang tot alle festiviteiten",
                    "1x Trouwalbum (€250)",
                ),
            ),
            Pledge(
                name = "vip+",
                orderId = 6,
                price = BigDecimal.valueOf(600),
                description = "Diamanten zijn voor altijd en zo ook het album bij deze pledge. De " +
                    "fotograaf drukt het finale fotoboek op ultra duurzaam materiaal. Zo kun je onze " +
                    "mooie dag, nog door enkele generaties na ons laten herbeleven.",
                contents = listOf(
                    "2x Toegang tot alle festiviteiten",
                    "1x Diamanten Trouwalbum (€600)",
                ),
            ),
            Pledge(
                name = "yolo",
                orderId = 7,
                price = BigDecimal.valueOf(10_000),
                description = "Hou jij van uitdagingen? Wij ook! Voor deze pledge zal de bruidegom één " +
                    "politiek correct woord naar keuze verwerken in één van zijn speeches. Niet geldig " +
                    "voor merk getinte woorden. Zie hiervoor de commerciële pledge.",
                contents = listOf(
                    "2x Toegang tot alle festiviteiten",
                    "1x Politiek correct woord naar keuze in speech bruidegom",
                ),
            ),
            Pledge(
                name = "commercial",
                orderId = 8,
                price = BigDecimal.valueOf(20_000),
                description = "Misschien heb jij een product waar je graag reclame voor wilt maken. " +
                    "Denk bijvoorbeeld aan het heerlijke bier Valduc. Met deze pledge krijg je een " +
                    "vermelding op deze site of tijdens één van de speeches.",
                contents = listOf(
                    "2x Toegang tot alle festiviteiten",
                    "1x Vermelding van jouw product",
                ),
            ),
        )
    }
}
