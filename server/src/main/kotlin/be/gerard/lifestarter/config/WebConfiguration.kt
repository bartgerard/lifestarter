package be.gerard.lifestarter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerTypePredicate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@ConfigurationProperties(prefix = "lifestarter.web")
data class WebProperties(
    val allowedOrigins: List<String> = listOf("http://localhost:4200"),
)

/**
 * Web-tier wiring shared by every controller.
 *
 * The `/api` prefix is applied centrally so no controller repeats it. This service is a pure JSON
 * API: the Angular bundle is served by its own nginx container, which reverse-proxies `/api` here.
 */
@Configuration(proxyBeanMethods = false)
class WebConfiguration(
    private val properties: WebProperties,
) : WebMvcConfigurer {

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        configurer.addPathPrefix(
            API_PREFIX,
            HandlerTypePredicate.forBasePackage("be.gerard.lifestarter"),
        )
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("$API_PREFIX/**")
            .allowedOrigins(*properties.allowedOrigins.toTypedArray())
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

    private companion object {
        const val API_PREFIX = "/api"
    }
}
