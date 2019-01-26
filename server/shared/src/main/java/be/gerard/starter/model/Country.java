package be.gerard.starter.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Country
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor
@Getter
@Builder
public class Country {
    private final String iso2;
    private final String iso3;
    private final String name;
}
