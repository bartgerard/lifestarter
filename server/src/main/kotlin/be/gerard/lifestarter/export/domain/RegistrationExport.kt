package be.gerard.lifestarter.export.domain

import be.gerard.lifestarter.access.domain.Bouncer
import be.gerard.lifestarter.registration.domain.Registration

/**
 * A rendered export, ready to be streamed to the caller.
 *
 * Deliberately not a `data class`: [content] is a [ByteArray] and structural equality on it would
 * be misleading.
 */
class RegistrationExport(
    val fileName: String,
    val contentType: String,
    val content: ByteArray,
) {
    val size: Int
        get() = content.size
}

/** Outbound port that turns the raw data into a document; the format is an adapter concern. */
fun interface RegistrationExporter {
    fun render(registrations: List<Registration>, bouncers: List<Bouncer>): ByteArray
}
