package be.gerard.starter.service;

import be.gerard.starter.model.Registration;
import be.gerard.starter.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * RegistrationService
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    public Flux<Registration> findAll() {
        return Flux.fromIterable(registrationRepository.findAll());
    }

    public Mono<Registration> save(
            final Registration registration
    ) {
        return Mono.just(registrationRepository.save(registration));
    }

    public long nbGuests() {
        // Anyone: Hmm? Can't you count within the DB itself?
        // Me: Probably, but neither the tools or documentation make it obvious how this can be done.
        return registrationRepository.findAll()
                .stream()
                .map(Registration::getGuests)
                .mapToLong(List::size)
                .sum();
    }

}
