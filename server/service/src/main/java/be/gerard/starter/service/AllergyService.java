package be.gerard.starter.service;

import be.gerard.starter.model.Allergy;

import java.util.Set;

/**
 * AllergyService
 *
 * @author bartgerard
 * @version v0.0.1
 */
public class AllergyService {

    private static final Set<Allergy> ALLERGIES = Set.of(
            Allergy.of("Eggs"),
            Allergy.of("Milk"),
            Allergy.of("Peanuts"),
            Allergy.of("Tree nuts"),
            Allergy.of("Fish"),
            Allergy.of("Shellfish"),
            Allergy.of("Wheat"),
            Allergy.of("Soy")
    );

}
