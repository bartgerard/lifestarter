package be.gerard.starter.service;

import be.gerard.starter.model.Registration;
import be.gerard.starter.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

}
