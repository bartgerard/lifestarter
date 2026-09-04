package be.gerard.lifestarter.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class OpenApiConfiguration {

    @Bean
    fun lifestarterOpenApi(): OpenAPI = OpenAPI().info(
        Info()
            .title("Lifestarter API")
            .description("RSVP and guest management for the Lifestarter wedding site.")
            .version("1.0.0")
            .license(License().name("MIT")),
    )
}
