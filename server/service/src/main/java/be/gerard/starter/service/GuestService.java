package be.gerard.starter.service;

import be.gerard.starter.model.Guest;
import be.gerard.starter.value.FullName;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;

/**
 * GuestService
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Service
public class GuestService {

    private static final List<Guest> GUESTS = List.of(
            Guest.builder()
                    .fullName(FullName.builder()
                            .firstName("James")
                            .lastName("Bond")
                            .build()
                    )
                    .build(),
            Guest.builder()
                    .fullName(FullName.builder()
                            .firstName("Bruce")
                            .lastName("Wayne")
                            .build()
                    )
                    .build()
    );

    public Flux<Guest> findAll() {
        return Flux.fromIterable(GUESTS);
    }

    public Flux<Guest> findAllSlowly() {
        final Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        return Flux.zip(
                interval,
                Flux.fromIterable(GUESTS)
        )
                .map(Tuple2::getT2);
    }

}