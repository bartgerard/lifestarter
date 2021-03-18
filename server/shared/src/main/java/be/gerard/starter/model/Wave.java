package be.gerard.starter.model;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

/**
 * Wave
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Value(staticConstructor = "of")
@EqualsAndHashCode(of = "label")
public class Wave {

    @Id
    private final String label;

    private final LocalDate deadline;

}
