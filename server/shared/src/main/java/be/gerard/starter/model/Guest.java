package be.gerard.starter.model;

import be.gerard.starter.value.FullName;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;

/**
 * Guest
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor
@Getter
@Builder
public class Guest {

    private final FullName fullName;

    private final Diet diet;

    @Singular
    private final List<String> allergies;

}
