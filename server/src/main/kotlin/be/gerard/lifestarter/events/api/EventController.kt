package be.gerard.lifestarter.events.api

import be.gerard.lifestarter.events.adapter.RegistrationEventBroadcaster
import be.gerard.lifestarter.events.domain.Heartbeat
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.time.Clock
import java.time.Duration
import java.time.Instant

@RestController
@RequestMapping("events")
@Tag(name = "events", description = "Liveness ping and the server-sent event feed")
class EventController(
    private val broadcaster: RegistrationEventBroadcaster,
    private val clock: Clock,
) {

    @GetMapping("ping")
    @Operation(summary = "Cheap liveness probe for the front-end.")
    fun ping(): Heartbeat = Heartbeat(message = "pong", timestamp = Instant.now(clock))

    @GetMapping("stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(summary = "Subscribe to live RSVP notifications.")
    fun stream(): SseEmitter = broadcaster.subscribe(STREAM_TIMEOUT.toMillis())

    private companion object {
        val STREAM_TIMEOUT: Duration = Duration.ofMinutes(30)
    }
}
