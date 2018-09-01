package be.gerard.starter.web;

import be.gerard.starter.model.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * GuestRestController
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RestController
@RequiredArgsConstructor
public class SseRestController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("feed")
    public Mono<Info> ping() {
        return Mono.just(Info.of("pong"));
    }

    @GetMapping("random")
    public Flux<ServerSentEvent<Info>> random() {
        return Flux.interval(Duration.ofSeconds(1L))
                .map(s -> ServerSentEvent.<Info>builder()
                        .data(Info.of(Objects.toString(s)))
                        .build()
                );
    }

    @GetMapping("stream")
    public SseEmitter stream() {
        final SseEmitter emitter = new SseEmitter();

        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));

        return emitter;
    }

}
