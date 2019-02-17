package be.gerard.starter.web;

import be.gerard.starter.model.Pledge;
import be.gerard.starter.service.PledgeService;
import be.gerard.starter.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * PledgeRestController
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RestController
@RequestMapping("pledges")
@RequiredArgsConstructor
public class PledgeRestController {

    private final PledgeService pledgeService;
    private final RegistrationService registrationService;

    @GetMapping
    public Flux<Pledge> pledges() {
        final Map<String, Integer> pledgeMap = registrationService.nbGuestsPerPledge();

        return pledgeService.findAll()
                .map(pledge -> pledge.toBuilder()
                        .amount(pledgeMap.getOrDefault(pledge.getName(), 0))
                        .build()
                );
    }

}
