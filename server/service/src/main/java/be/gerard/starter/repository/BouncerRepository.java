package be.gerard.starter.repository;

import be.gerard.starter.model.Bouncer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * VipRepository
 *
 * @author bartgerard
 * @version v0.0.1
 */
public interface BouncerRepository extends MongoRepository<Bouncer, String> {

    Optional<Bouncer> findByFirstNameLikeAndLastNameLike(
            String firstName,
            String lastName
    );

}
