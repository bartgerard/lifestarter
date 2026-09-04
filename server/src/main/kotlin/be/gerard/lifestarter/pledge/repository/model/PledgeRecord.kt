package be.gerard.lifestarter.pledge.repository.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

/** Mirrors the historical `pledge` collection. */
@Document(collection = "pledge")
data class PledgeRecord(
    @Id val name: String,
    val orderId: Int = 0,
    val price: BigDecimal = BigDecimal.ZERO,
    val description: String = "",
    val contents: List<String> = emptyList(),
    val limit: Int = 0,
    val available: Boolean = true,
)
