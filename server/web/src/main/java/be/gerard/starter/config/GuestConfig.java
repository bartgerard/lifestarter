package be.gerard.starter.config;

import be.gerard.starter.model.Guest;
import be.gerard.starter.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * GuestConfig
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Configuration
@RequiredArgsConstructor
public class GuestConfig {

    private final GuestService guestService;

    @Bean
    public RouterFunction<?> routes() {
        return nest(
                path("/guests"),
                nest(
                        accept(MediaType.APPLICATION_JSON),
                        route(GET("/"), this::guests)
                )
        );
    }

    private Mono<ServerResponse> guests(
            final ServerRequest request
    ) {
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(guestService.findAll(), Guest.class);
    }

}
