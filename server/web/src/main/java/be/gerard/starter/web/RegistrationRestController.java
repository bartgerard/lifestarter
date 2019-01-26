package be.gerard.starter.web;

import be.gerard.starter.model.Registration;
import be.gerard.starter.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * RegistrationRestController
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RestController
@RequestMapping("registrations")
@RequiredArgsConstructor
public class RegistrationRestController {

    private final RegistrationService registrationService;

    @GetMapping
    public Flux<Registration> registrations() {
        return registrationService.findAll();
    }

    @PutMapping
    public Mono<Registration> addRegistration(
            final Registration registration
    ) {
        return registrationService.save(registration);
    }

}
