package be.gerard.lifestarter.pledge.repository

import be.gerard.lifestarter.pledge.domain.Pledge
import be.gerard.lifestarter.pledge.domain.PledgeRepository
import be.gerard.lifestarter.pledge.repository.model.PledgeRecord
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class MongoPledgeRepository(
    private val mongo: MongoOperations,
) : PledgeRepository {

    override fun findAll(): List<Pledge> = mongo.findAll(PledgeRecord::class.java)
        .map(::toDomain)
        .sortedBy(Pledge::orderId)

    override fun findByName(name: String): Pledge? =
        mongo.findById(name, PledgeRecord::class.java)?.let(::toDomain)

    override fun saveAll(pledges: List<Pledge>) {
        pledges.asSequence()
            .filterNot { mongo.exists(Query(Criteria.where("_id").`is`(it.name)), PledgeRecord::class.java) }
            .map(::toRecord)
            .forEach(mongo::insert)
    }

    private fun toDomain(record: PledgeRecord): Pledge = Pledge(
        name = record.name,
        orderId = record.orderId,
        price = record.price,
        description = record.description,
        contents = record.contents,
        limit = record.limit,
        available = record.available,
    )

    private fun toRecord(pledge: Pledge): PledgeRecord = PledgeRecord(
        name = pledge.name,
        orderId = pledge.orderId,
        price = pledge.price,
        description = pledge.description,
        contents = pledge.contents,
        limit = pledge.limit,
        available = pledge.available,
    )
}
