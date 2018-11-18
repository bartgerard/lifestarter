package be.gerard.starter.repository;

import be.gerard.starter.model.Pledge;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * PledgeRepository
 *
 * @author bartgerard
 * @version v0.0.1
 */
public interface PledgeRepository extends MongoRepository<Pledge, String> {

}
