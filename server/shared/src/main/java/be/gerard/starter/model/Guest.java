package be.gerard.starter.model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

/**
 * Guest
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Value
@Builder
public class Guest {

    private final String role;

    private final String firstName;

    private final String lastName;

    private final Diet diet;

    @Singular
    private final List<String> allergies;

    private final String comment;

}
