package be.gerard.lifestarter.access.repository

import be.gerard.lifestarter.access.domain.Bouncer
import be.gerard.lifestarter.access.domain.BouncerRepository
import be.gerard.lifestarter.access.domain.Vip
import be.gerard.lifestarter.access.domain.VipRepository
import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.PersonName
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.transaction.annotation.Transactional

/**
 * Class-level `@Transactional` is deliberate: besides the obvious atomicity, it is what makes the
 * Kotlin all-open plugin unseal the class so Spring can proxy it. Reads default to `readOnly`.
 */
@Transactional(readOnly = true)
class SqliteVipRepository(
    private val jdbc: JdbcClient,
) : VipRepository {

    override fun findByName(name: PersonName): Vip? {
        val normalised = name.normalised()

        val roles = jdbc
            .sql("SELECT role FROM vip_role WHERE first_name = :firstName AND last_name = :lastName ORDER BY role")
            .param("firstName", normalised.firstName)
            .param("lastName", normalised.lastName)
            .query { rs, _ -> rs.getString("role") }
            .list()

        val exists = jdbc
            .sql("SELECT 1 FROM vip WHERE first_name = :firstName AND last_name = :lastName")
            .param("firstName", normalised.firstName)
            .param("lastName", normalised.lastName)
            .query(Int::class.java)
            .optional()
            .isPresent

        return if (exists) Vip(normalised, roles) else null
    }

    override fun findAll(): List<Vip> {
        val rolesByName = jdbc.sql("SELECT first_name, last_name, role FROM vip_role ORDER BY role")
            .query { rs, _ -> PersonName(rs.getString("first_name"), rs.getString("last_name")) to rs.getString("role") }
            .list()
            .groupBy({ it.first }, { it.second })

        return jdbc.sql("SELECT first_name, last_name FROM vip ORDER BY last_name, first_name")
            .query { rs, _ -> PersonName(rs.getString("first_name"), rs.getString("last_name")) }
            .list()
            .map { name -> Vip(name, rolesByName[name].orEmpty()) }
    }

    @Transactional
    override fun saveAll(vips: List<Vip>) {
        vips.forEach { vip ->
            val name = vip.name.normalised()

            jdbc.sql("INSERT OR IGNORE INTO vip (first_name, last_name) VALUES (:firstName, :lastName)")
                .param("firstName", name.firstName)
                .param("lastName", name.lastName)
                .update()

            vip.roles.distinct().forEach { role ->
                jdbc.sql(
                    """
                    INSERT OR IGNORE INTO vip_role (first_name, last_name, role)
                    VALUES (:firstName, :lastName, :role)
                    """
                )
                    .param("firstName", name.firstName)
                    .param("lastName", name.lastName)
                    .param("role", role)
                    .update()
            }
        }
    }
}

@Transactional(readOnly = true)
class SqliteBouncerRepository(
    private val jdbc: JdbcClient,
) : BouncerRepository {

    override fun findByName(name: PersonName): Bouncer? {
        val normalised = name.normalised()

        val activities = jdbc
            .sql(
                """
                SELECT activity FROM bouncer_activity
                WHERE first_name = :firstName AND last_name = :lastName
                """
            )
            .param("firstName", normalised.firstName)
            .param("lastName", normalised.lastName)
            .query { rs, _ -> Activity.valueOf(rs.getString("activity")) }
            .list()
            .sorted()

        val exists = jdbc
            .sql("SELECT 1 FROM bouncer WHERE first_name = :firstName AND last_name = :lastName")
            .param("firstName", normalised.firstName)
            .param("lastName", normalised.lastName)
            .query(Int::class.java)
            .optional()
            .isPresent

        return if (exists) Bouncer(normalised, activities) else null
    }

    override fun findAll(): List<Bouncer> {
        val activitiesByName = jdbc.sql("SELECT first_name, last_name, activity FROM bouncer_activity")
            .query { rs, _ ->
                PersonName(rs.getString("first_name"), rs.getString("last_name")) to
                    Activity.valueOf(rs.getString("activity"))
            }
            .list()
            .groupBy({ it.first }, { it.second })

        return jdbc.sql("SELECT first_name, last_name FROM bouncer ORDER BY last_name, first_name")
            .query { rs, _ -> PersonName(rs.getString("first_name"), rs.getString("last_name")) }
            .list()
            .map { name -> Bouncer(name, activitiesByName[name].orEmpty().sorted()) }
    }

    @Transactional
    override fun saveAll(bouncers: List<Bouncer>) {
        bouncers.forEach { bouncer ->
            val name = bouncer.name.normalised()

            jdbc.sql("INSERT OR IGNORE INTO bouncer (first_name, last_name) VALUES (:firstName, :lastName)")
                .param("firstName", name.firstName)
                .param("lastName", name.lastName)
                .update()

            bouncer.activities.distinct().forEach { activity ->
                jdbc.sql(
                    """
                    INSERT OR IGNORE INTO bouncer_activity (first_name, last_name, activity)
                    VALUES (:firstName, :lastName, :activity)
                    """
                )
                    .param("firstName", name.firstName)
                    .param("lastName", name.lastName)
                    .param("activity", activity.name)
                    .update()
            }
        }
    }
}
