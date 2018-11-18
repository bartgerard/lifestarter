package be.gerard.starter.web;

import be.gerard.starter.model.Pledge;
import be.gerard.starter.service.PledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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

    @GetMapping
    public Flux<Pledge> pledges() {
        return pledgeService.findAll();
    }
    
}
