package be.gerard.starter.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pledge
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RequiredArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "name")
public class Pledge {

    @Id
    private final String name;

    private final int orderId;

    private final BigDecimal price;

    private final String description;

    @Singular
    private final List<String> contents;

    private final int limit;

    private final boolean available;

    private final int amount;

}
