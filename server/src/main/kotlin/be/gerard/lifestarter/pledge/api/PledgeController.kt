package be.gerard.lifestarter.pledge.api

import be.gerard.lifestarter.pledge.domain.PledgeOffer
import be.gerard.lifestarter.pledge.domain.PledgeService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

/** A pledge as offered to the RSVP form, including how many guests already picked it. */
data class PledgeTo(
    val name: String,
    val orderId: Int,
    val price: BigDecimal,
    val description: String,
    val contents: List<String>,
    val limit: Int,
    val available: Boolean,
    val amount: Int,
)

@RestController
@RequestMapping("pledges")
@Tag(name = "pledges", description = "The tiers guests can sign up for")
class PledgeController(
    private val pledges: PledgeService,
) {

    @GetMapping
    fun pledges(): List<PledgeTo> = pledges.findAllOffers().map(::toTo)

    private fun toTo(offer: PledgeOffer): PledgeTo = PledgeTo(
        name = offer.pledge.name,
        orderId = offer.pledge.orderId,
        price = offer.pledge.price,
        description = offer.pledge.description,
        contents = offer.pledge.contents,
        limit = offer.pledge.limit,
        available = offer.isSelectable,
        amount = offer.subscribedGuests,
    )
}
