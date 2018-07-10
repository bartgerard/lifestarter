package be.gerard.starter.service;

import be.gerard.starter.model.Guest;
import be.gerard.starter.value.FullName;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
                    .build()
    );

    public Flux<Guest> findAll() {
        return Flux.fromIterable(GUESTS);
    }

}
