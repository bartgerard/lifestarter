package be.gerard.starter.model;

import be.gerard.starter.value.Activity;
import lombok.Value;

import java.util.List;

/**
 * Bouncer
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Value
public class Bouncer {
    private final String firstName;
    private final String lastName;
    private final List<Activity> activities;
}
