package be.gerard.starter.model;

import lombok.Value;

import java.util.List;

/**
 * Vip
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Value
public class Vip {
    private final String firstName;
    private final String lastName;
    private final List<String> roles;
}
