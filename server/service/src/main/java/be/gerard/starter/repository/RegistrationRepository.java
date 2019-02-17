package be.gerard.starter.repository;

import be.gerard.starter.model.Registration;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * RegistrationRepository
 *
 * @author bartgerard
 * @version v0.0.1
 */
public interface RegistrationRepository extends MongoRepository<Registration, String> {

    //@Query(value = "{'pledgeName' : ?0}",fields = "{'pledgeName' : 1}")
    //db.registration.find({$unwind: '$guests'})

}
