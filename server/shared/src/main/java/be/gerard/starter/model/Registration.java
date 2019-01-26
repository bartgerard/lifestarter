package be.gerard.starter.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Registration
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor
@Getter
@Builder
@EqualsAndHashCode(of = "email")
public class Registration {

    @Id
    private final String email;

    @Singular
    private final List<Guest> guests;

}
