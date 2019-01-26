package be.gerard.starter.web;

import be.gerard.starter.model.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * GuestRestController
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RestController
@RequestMapping("countries")
@RequiredArgsConstructor
public class CountryRestController {

    @GetMapping
    public Mono<List<Country>> diets(
            final Locale myLocale
    ) {
        return Flux.just(Locale.getISOCountries())
                .map(iso -> new Locale("", iso))
                .map(locale -> Country.builder()
                        .iso2(locale.getCountry())
                        .iso3(locale.getISO3Country())
                        .name(locale.getDisplayCountry(myLocale))
                        .build()
                )
                .sort(Comparator.comparing(Country::getName))
                .collectList();
    }

}
