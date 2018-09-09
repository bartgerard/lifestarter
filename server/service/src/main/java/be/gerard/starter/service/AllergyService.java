package be.gerard.starter.service;

import be.gerard.starter.model.Allergy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/**
 * AllergyService
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Service
public class AllergyService {

    private static final List<Allergy> ALLERGIES = Arrays.asList(
            Allergy.of("EGGS"),
            Allergy.of("MILK"),
            Allergy.of("PEANUTS"),
            Allergy.of("TREE_NUTS"), // almonds, walnuts, pecans
            Allergy.of("FISH"),
            Allergy.of("SHELLFISH"), // CRUSTACEAN SHELLFISH crab, lobster, shrimp
            Allergy.of("WHEAT"),
            Allergy.of("SOY"),
            Allergy.of("PAPRIKA"),
            Allergy.of("PAPRIKA_POWDER")
    );

    public Flux<Allergy> findAll() {
        return Flux.fromIterable(ALLERGIES);
    }

}
