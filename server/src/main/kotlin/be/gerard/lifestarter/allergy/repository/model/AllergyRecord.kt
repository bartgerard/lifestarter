package be.gerard.lifestarter.allergy.repository.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/** Mirrors the historical `allergy` collection so existing documents keep working. */
@Document(collection = "allergy")
data class AllergyRecord(
    @Id val id: String,
)
