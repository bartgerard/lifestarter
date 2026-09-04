package be.gerard.lifestarter.allergy.repository

import be.gerard.lifestarter.allergy.domain.Allergy
import be.gerard.lifestarter.allergy.domain.AllergyRepository
import org.springframework.jdbc.core.simple.JdbcClient

class SqliteAllergyRepository(
    private val jdbc: JdbcClient,
) : AllergyRepository {

    override fun findAll(): List<Allergy> = jdbc.sql("SELECT id FROM allergy")
        .query { rs, _ -> Allergy(rs.getString("id")) }
        .list()

    override fun saveAll(allergies: List<Allergy>) {
        allergies.forEach { allergy ->
            jdbc.sql("INSERT OR IGNORE INTO allergy (id) VALUES (:id)")
                .param("id", allergy.id)
                .update()
        }
    }
}
