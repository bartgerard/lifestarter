package be.gerard.starter.web;

import be.gerard.starter.command.AddRegistration;
import be.gerard.starter.event.RegistrationAdded;
import be.gerard.starter.model.Registration;
import be.gerard.starter.service.RegistrationService;
import be.gerard.starter.service.VipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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
    private final VipService vipService;

    // TODO HIDE FROM USERS... GDPR
    /*
    @GetMapping
    public Flux<Registration> registrations() {
        return registrationService.findAll();
    }
    */

    @PutMapping
    public Mono<RegistrationAdded> addRegistration(
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

    @GetMapping("vip-check")
    public List<String> extraRoles(
            @RequestParam("first-name") final String firstName,
            @RequestParam("last-name") final String lastName
    ) {
        return vipService.findRoles(firstName, lastName);
    }

    @GetMapping("nb-guests")
    public long nbGuests() {
        return registrationService.nbGuests();
    }

    @GetMapping("nb-guests-by-pledge")
    public Map<String, Integer> nbGuestsPerPledge() {
        return registrationService.nbGuestsPerPledge();
    }

}
