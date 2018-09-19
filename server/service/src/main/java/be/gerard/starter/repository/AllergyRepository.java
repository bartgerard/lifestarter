package be.gerard.starter.repository;

import be.gerard.starter.model.Allergy;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * AllergyRepository
 *
 * @author bartgerard
 * @version v0.0.1
 */
public interface AllergyRepository extends MongoRepository<Allergy, String> {

}
