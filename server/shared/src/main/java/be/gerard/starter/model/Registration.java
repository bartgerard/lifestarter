package be.gerard.starter.model;

import be.gerard.starter.value.Activity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Registration
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Value
@Builder
@EqualsAndHashCode(of = "email")
public class Registration {

    @Id
    private final String email;

    @Singular
    private final List<Guest> guests;

    @Singular
    private final List<ContactOption> contactOptions;

    private final String pledgeName;

    @Singular
    private final List<Activity> activities;

}
