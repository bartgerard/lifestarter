package be.gerard.starter.web;

import be.gerard.starter.model.Allergy;
import be.gerard.starter.service.AllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * GuestRestController
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RestController
@RequestMapping("allergies")
@RequiredArgsConstructor
public class AllergyRestController {

    private final AllergyService allergyService;

    @GetMapping
    public Mono<List<String>> allergies() {
        return allergyService.findAll()
                .map(Allergy::getName)
                .collectList();
    }

}
