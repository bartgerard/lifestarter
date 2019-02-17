package be.gerard.starter.service;

import be.gerard.starter.event.RegistrationAdded;
import be.gerard.starter.model.Registration;
import be.gerard.starter.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final ApplicationEventPublisher eventPublisher;

    public Flux<Registration> findAll() {
        return Flux.fromIterable(registrationRepository.findAll());
    }

    public Mono<RegistrationAdded> save(
            final Registration registration
    ) {
        return Mono.just(registrationRepository.save(registration))
                .map(RegistrationAdded::of)
                .doOnSuccess(eventPublisher::publishEvent);
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

    public Map<String, Integer> nbGuestsPerPledge() {
        // Anyone: Hmm? Can't you count within the DB itself?
        // Me: Probably, but neither the tools or documentation make it obvious how this can be done.
        return registrationRepository.findAll()
                .stream()
                .filter(registration -> Objects.nonNull(registration.getPledgeName()))
                .collect(Collectors.groupingBy(
                        Registration::getPledgeName,
                        Collectors.summingInt(registration -> registration.getGuests().size())
                ));
    }

}
