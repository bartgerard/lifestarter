package be.gerard.starter.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

/**
 * User
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor
@Getter
@ToString(of = "email")
public class User {

    @Id
    @NonNull
    private final String email;

    @NonNull
    private final String password;

    @NonNull
    private final String firstName;

    @NonNull
    private final String lastName;

}
