package be.gerard.starter.model;

import be.gerard.starter.value.FullName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Guest
 *
 * @author bartgerard
 * @version v0.0.1
 */
@AllArgsConstructor
@Getter
@Builder
public class Guest {
    private FullName fullName;
}
