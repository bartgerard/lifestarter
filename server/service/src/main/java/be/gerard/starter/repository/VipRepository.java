package be.gerard.starter.repository;

import be.gerard.starter.model.Vip;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * VipRepository
 *
 * @author bartgerard
 * @version v0.0.1
 */
public interface VipRepository extends MongoRepository<Vip, String> {

    Optional<Vip> findByFirstNameLikeAndLastNameLike(
            final String firstName,
            final String lastName
    );

}
