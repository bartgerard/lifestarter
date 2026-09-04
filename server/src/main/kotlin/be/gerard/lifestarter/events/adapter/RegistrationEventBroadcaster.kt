package be.gerard.lifestarter.events.adapter

import be.gerard.lifestarter.registration.domain.RegistrationAdded
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Fans domain events out to every browser currently listening on the SSE feed.
 *
 * A [CopyOnWriteArrayList] fits the access pattern: subscriptions are rare, broadcasts iterate.
 * Emitters that fail to accept a message are already dead, so they are dropped on the spot.
 */
@Component
class RegistrationEventBroadcaster {

    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    fun subscribe(timeoutMillis: Long): SseEmitter {
        val emitter = SseEmitter(timeoutMillis)

        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitters.remove(emitter) }
        emitter.onError { emitters.remove(emitter) }

        emitters += emitter
        log.debug("SSE subscriber joined, {} connected", emitters.size)

        return emitter
    }

    @EventListener
    fun onRegistrationAdded(event: RegistrationAdded) {
        broadcast("registration-added", event.registration.email)
    }

    fun broadcast(name: String, payload: Any) {
        emitters.forEach { emitter ->
            runCatching { emitter.send(SseEmitter.event().name(name).data(payload)) }
                .onFailure { failure ->
                    emitters.remove(emitter)
                    if (failure !is IOException && failure !is IllegalStateException) throw failure
                }
        }
    }

    private companion object {
        val log = LoggerFactory.getLogger(RegistrationEventBroadcaster::class.java)
    }
}
