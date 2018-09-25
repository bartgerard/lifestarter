package be.gerard.starter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

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
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4200"
                );
    }

    // Thx to https://stackoverflow.com/questions/46148843/spring-boot-angular-4-routing-in-app-hits-the-server#
    @Override
    public void addResourceHandlers(
            final ResourceHandlerRegistry registry
    ) {
        registry.addResourceHandler("/**/*")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(
                            String resourcePath,
                            Resource location
                    ) throws IOException {
                        final Resource requestedResource = location.createRelative(resourcePath);
                        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource : new ClassPathResource("/static/index.html");
                    }
                });
    }

}
