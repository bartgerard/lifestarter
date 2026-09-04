package be.gerard.lifestarter.wave.repository

import be.gerard.lifestarter.wave.domain.Wave
import be.gerard.lifestarter.wave.domain.WaveRepository
import be.gerard.lifestarter.wave.repository.model.WaveRecord
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class MongoWaveRepository(
    private val mongo: MongoOperations,
) : WaveRepository {

    override fun findAll(): List<Wave> = mongo.findAll(WaveRecord::class.java)
        .map { Wave(it.label, it.deadline) }

    override fun saveAll(waves: List<Wave>) {
        waves.asSequence()
            .filterNot { mongo.exists(Query(Criteria.where("_id").`is`(it.label)), WaveRecord::class.java) }
            .forEach { mongo.insert(WaveRecord(it.label, it.deadline)) }
    }
}
