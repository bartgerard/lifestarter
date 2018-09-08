package be.gerard.starter.service;

import be.gerard.starter.model.Allergy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * AllergyService
 *
 * @author bartgerard
 * @version v0.0.1
 */
public class AllergyService {

    private static final Set<Allergy> ALLERGIES = new HashSet<>(Arrays.asList(
            Allergy.of("Eggs"),
            Allergy.of("Milk"),
            Allergy.of("Peanuts"),
            Allergy.of("Tree nuts"),
            Allergy.of("Fish"),
            Allergy.of("Shellfish"),
            Allergy.of("Wheat"),
            Allergy.of("Soy")
    ));

}
