package be.gerard.starter.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Info
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor(staticName = "of")
@Getter
public class Info {

    private final String message;

    private final LocalDateTime timestamp = LocalDateTime.now();

}
