package be.gerard.starter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ExportProperties
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Configuration
//@PropertySource("...")
@ConfigurationProperties(prefix = "life-starter.export")
@Getter
@Setter
public class ExportProperties {
    private String uri;
}
