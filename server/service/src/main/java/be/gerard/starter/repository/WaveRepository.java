package be.gerard.starter.repository;

import be.gerard.starter.model.Wave;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * AllergyRepository
 *
 * @author bartgerard
 * @version v0.0.1
 */
public interface WaveRepository extends MongoRepository<Wave, String> {

}
