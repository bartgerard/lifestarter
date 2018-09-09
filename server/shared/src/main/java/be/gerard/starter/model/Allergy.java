package be.gerard.starter.model;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = "name")
public class Allergy {
    private final String name;
}
