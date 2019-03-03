package be.gerard.starter.service;

import be.gerard.starter.model.Wave;
import be.gerard.starter.repository.WaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * AllergyService
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Service
@RequiredArgsConstructor
public class WaveService {

    private static final List<Wave> WAVES = Arrays.asList(
            Wave.of("1st", LocalDate.of(2019, 3, 1)),
            Wave.of("2nd", LocalDate.of(2019, 4, 1)),
            Wave.of("3rd", LocalDate.of(2019, 4, 20))
    );

    private final WaveRepository waveRepository;

    public Flux<Wave> findAll() {
        return Flux.fromIterable(waveRepository.findAll());
    }

    //@PostConstruct
    public void init() {
        waveRepository.saveAll(WAVES);
    }

}
