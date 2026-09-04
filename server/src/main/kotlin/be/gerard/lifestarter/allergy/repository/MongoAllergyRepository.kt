package be.gerard.lifestarter.allergy.repository

import be.gerard.lifestarter.allergy.domain.Allergy
import be.gerard.lifestarter.allergy.domain.AllergyRepository
import be.gerard.lifestarter.allergy.repository.model.AllergyRecord
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class MongoAllergyRepository(
    private val mongo: MongoOperations,
) : AllergyRepository {

    override fun findAll(): List<Allergy> = mongo.findAll(AllergyRecord::class.java)
        .map { Allergy(it.id) }

    override fun saveAll(allergies: List<Allergy>) {
        allergies.asSequence()
            .filterNot { mongo.exists(Query(Criteria.where("_id").`is`(it.id)), AllergyRecord::class.java) }
            .forEach { mongo.insert(AllergyRecord(it.id)) }
    }
}
