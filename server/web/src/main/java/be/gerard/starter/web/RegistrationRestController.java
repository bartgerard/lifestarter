package be.gerard.starter.web;

import be.gerard.starter.command.AddRegistration;
import be.gerard.starter.model.Registration;
import be.gerard.starter.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // TODO HIDE FROM USERS... GDPR
    @GetMapping
    public Flux<Registration> registrations() {
        return registrationService.findAll();
    }

    @PutMapping
    public Mono<Registration> addRegistration(
            @RequestBody final AddRegistration command
    ) {
        final Registration registration = Registration.builder()
                .email(command.getEmail())
                .guests(command.getGuests())
                .contactOptions(command.getContactOptions())
                .pledgeName(command.getPledgeName())
                .build();

        return registrationService.save(registration);
    }

    @GetMapping("guests")
    public long nbGuests() {
        return registrationService.nbGuests();
    }

}
