package be.gerard.starter.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Allergy
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor(staticName = "of")
@Getter
public class Allergy {
    private final String name;
}
