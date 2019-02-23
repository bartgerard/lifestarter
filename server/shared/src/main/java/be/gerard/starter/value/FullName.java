package be.gerard.starter.value;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * FullName
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor(staticName = "of")
@Getter
@Builder
public class FullName {
    private final String firstName;
    private final String lastName;
}
