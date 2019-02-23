package be.gerard.starter.command;

import be.gerard.starter.model.ContactOption;
import be.gerard.starter.model.Guest;
import lombok.Value;

import java.util.List;

/**
 * AddRegistration
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Value
public class AddRegistration {
    private final String email;
    private final List<Guest> guests;
    private final List<ContactOption> contactOptions;
    private final String pledgeName;
    private final List<String> activities;
}
