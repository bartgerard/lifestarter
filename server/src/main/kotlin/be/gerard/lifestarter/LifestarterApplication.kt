package be.gerard.lifestarter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class LifestarterApplication

fun main(args: Array<String>) {
    runApplication<LifestarterApplication>(*args)
}
