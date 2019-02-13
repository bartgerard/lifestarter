package be.gerard.starter.web;

import be.gerard.starter.model.Wave;
import be.gerard.starter.service.WaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/**
 * WaveRestController
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RestController
@RequestMapping("waves")
@RequiredArgsConstructor
public class WaveRestController {

    private final WaveService waveService;

    @GetMapping("current")
    public Mono<Wave> currentWave() {
        return waveService.findAll()
                .sort(Comparator.comparing(Wave::getDeadline))
                .next();
    }

}
