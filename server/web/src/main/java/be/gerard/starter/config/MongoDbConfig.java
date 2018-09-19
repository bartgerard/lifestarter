package be.gerard.starter.config;

import be.gerard.starter.repository.AllergyRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * GuestConfig
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Configuration
// @EnableReactiveMongoRepositories(basePackageClasses = {
@EnableMongoRepositories(basePackageClasses = {
        AllergyRepository.class
})
public class MongoDbConfig {

}
