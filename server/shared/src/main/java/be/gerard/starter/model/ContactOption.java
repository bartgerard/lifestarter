package be.gerard.starter.model;

import lombok.Value;

/**
 * ContactInformation
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Value
public class ContactOption {

    private final String email;

    private final String address;

    private final String zipCode;

    private final String city;

    private final String countryIso3;

    private final String phoneNumber;

    private final Method contactMethod;

    public enum Method {
        NONE,
        EMAIL,
        PIGEON,
        GSM
    }

}
