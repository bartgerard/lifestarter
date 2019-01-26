package be.gerard.starter.web;

import be.gerard.starter.model.Diet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * GuestRestController
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RestController
@RequestMapping("diets")
@RequiredArgsConstructor
public class DietRestController {

    @GetMapping
    public Mono<List<Diet>> diets() {
        return Flux.just(Diet.values())
                .collectList();
    }

}
