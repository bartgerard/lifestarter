package be.gerard.starter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * GuestConfig
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(
            final CorsRegistry registry
    ) {
        registry.addMapping("/**");
    }

}
