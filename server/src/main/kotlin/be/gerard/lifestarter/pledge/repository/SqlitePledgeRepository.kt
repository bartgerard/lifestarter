package be.gerard.lifestarter.pledge.repository

import be.gerard.lifestarter.pledge.domain.Pledge
import be.gerard.lifestarter.pledge.domain.PledgeRepository
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

/**
 * Class-level `@Transactional` also makes the Kotlin all-open plugin unseal the class, which Spring
 * needs in order to proxy it. Reads default to `readOnly`.
 */
@Transactional(readOnly = true)
class SqlitePledgeRepository(
    private val jdbc: JdbcClient,
) : PledgeRepository {

    override fun findAll(): List<Pledge> {
        val contents = findContentsByPledgeName()

        return jdbc.sql(SELECT_PLEDGES)
            .query { rs, _ ->
                val name = rs.getString("name")
                Pledge(
                    name = name,
                    orderId = rs.getInt("order_id"),
                    price = BigDecimal(rs.getString("price")),
                    description = rs.getString("description"),
                    contents = contents[name].orEmpty(),
                    limit = rs.getInt("max_guests"),
                    available = rs.getBoolean("available"),
                )
            }
            .list()
    }

    override fun findByName(name: String): Pledge? = findAll().firstOrNull { it.name == name }

    @Transactional
    override fun saveAll(pledges: List<Pledge>) {
        pledges.forEach { pledge ->
            val inserted = jdbc.sql(INSERT_PLEDGE)
                .param("name", pledge.name)
                .param("orderId", pledge.orderId)
                .param("price", pledge.price.toPlainString())
                .param("description", pledge.description)
                .param("maxGuests", pledge.limit)
                .param("available", pledge.available)
                .update()

            // Contents belong to the pledge; only seed them alongside a freshly inserted row so an
            // operator's manual edits survive a restart.
            if (inserted == 0) return@forEach

            pledge.contents.forEachIndexed { position, content ->
                jdbc.sql(INSERT_PLEDGE_CONTENT)
                    .param("pledgeName", pledge.name)
                    .param("position", position)
                    .param("content", content)
                    .update()
            }
        }
    }

    private fun findContentsByPledgeName(): Map<String, List<String>> =
        jdbc.sql("SELECT pledge_name, content FROM pledge_content ORDER BY pledge_name, position")
            .query { rs, _ -> rs.getString("pledge_name") to rs.getString("content") }
            .list()
            .groupBy({ it.first }, { it.second })

    private companion object {
        const val SELECT_PLEDGES = """
            SELECT name, order_id, price, description, max_guests, available
            FROM pledge
            ORDER BY order_id
        """

        const val INSERT_PLEDGE = """
            INSERT OR IGNORE INTO pledge (name, order_id, price, description, max_guests, available)
            VALUES (:name, :orderId, :price, :description, :maxGuests, :available)
        """

        const val INSERT_PLEDGE_CONTENT = """
            INSERT OR IGNORE INTO pledge_content (pledge_name, position, content)
            VALUES (:pledgeName, :position, :content)
        """
    }
}
