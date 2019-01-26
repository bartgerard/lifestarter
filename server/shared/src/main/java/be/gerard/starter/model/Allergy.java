package be.gerard.starter.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * Allergy
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor(staticName = "of")
@Getter
@EqualsAndHashCode(of = "id")
public class Allergy {

    @Id
    private final String id;

}
