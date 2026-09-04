package be.gerard.lifestarter.events.domain

import java.time.Instant

/** A message pushed to connected clients over the SSE feed. */
data class Heartbeat(
    val message: String,
    val timestamp: Instant,
)
