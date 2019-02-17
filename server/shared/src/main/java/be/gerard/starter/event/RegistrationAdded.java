package be.gerard.starter.event;

import be.gerard.starter.model.Registration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * RegistrationAdded
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor(staticName = "of")
@Getter
public class RegistrationAdded {
    private final Registration registration;
}
