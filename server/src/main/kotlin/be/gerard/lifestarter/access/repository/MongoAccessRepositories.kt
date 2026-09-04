package be.gerard.lifestarter.access.repository

import be.gerard.lifestarter.access.domain.Bouncer
import be.gerard.lifestarter.access.domain.BouncerRepository
import be.gerard.lifestarter.access.domain.Vip
import be.gerard.lifestarter.access.domain.VipRepository
import be.gerard.lifestarter.access.repository.model.BouncerRecord
import be.gerard.lifestarter.access.repository.model.VipRecord
import be.gerard.lifestarter.registration.domain.PersonName
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * Case-insensitive exact match on a person's name.
 *
 * The original Java code lower-cased the input and used a `Like` query; matching on an anchored,
 * case-insensitive regex is equivalent but also tolerates documents that were stored with capitals.
 */
private fun nameQuery(name: PersonName): Query = Query(
    Criteria.where("firstName").regex("^${Regex.escape(name.firstName.trim())}$", "i")
        .and("lastName").regex("^${Regex.escape(name.lastName.trim())}$", "i")
)

class MongoVipRepository(
    private val mongo: MongoOperations,
) : VipRepository {

    override fun findByName(name: PersonName): Vip? =
        mongo.findOne(nameQuery(name), VipRecord::class.java)
            ?.let { Vip(PersonName(it.firstName, it.lastName), it.roles) }

    override fun findAll(): List<Vip> = mongo.findAll(VipRecord::class.java)
        .map { Vip(PersonName(it.firstName, it.lastName), it.roles) }

    override fun saveAll(vips: List<Vip>) {
        vips.asSequence()
            .filterNot { mongo.exists(nameQuery(it.name), VipRecord::class.java) }
            .map { vip ->
                val name = vip.name.normalised()
                VipRecord(name.firstName, name.lastName, vip.roles)
            }
            .forEach(mongo::insert)
    }
}

class MongoBouncerRepository(
    private val mongo: MongoOperations,
) : BouncerRepository {

    override fun findByName(name: PersonName): Bouncer? =
        mongo.findOne(nameQuery(name), BouncerRecord::class.java)
            ?.let { Bouncer(PersonName(it.firstName, it.lastName), it.activities.sorted()) }

    override fun findAll(): List<Bouncer> = mongo.findAll(BouncerRecord::class.java)
        .map { Bouncer(PersonName(it.firstName, it.lastName), it.activities.sorted()) }

    override fun saveAll(bouncers: List<Bouncer>) {
        bouncers.asSequence()
            .filterNot { mongo.exists(nameQuery(it.name), BouncerRecord::class.java) }
            .map { bouncer ->
                val name = bouncer.name.normalised()
                BouncerRecord(name.firstName, name.lastName, bouncer.activities)
            }
            .forEach(mongo::insert)
    }
}
