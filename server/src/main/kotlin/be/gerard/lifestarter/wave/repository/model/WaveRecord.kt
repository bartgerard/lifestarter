package be.gerard.lifestarter.wave.repository.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

/** Mirrors the historical `wave` collection. */
@Document(collection = "wave")
data class WaveRecord(
    @Id val label: String,
    val deadline: LocalDate,
)
