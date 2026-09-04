package be.gerard.lifestarter.wave.repository

import be.gerard.lifestarter.wave.domain.Wave
import be.gerard.lifestarter.wave.domain.WaveRepository
import org.springframework.jdbc.core.simple.JdbcClient
import java.time.LocalDate

class SqliteWaveRepository(
    private val jdbc: JdbcClient,
) : WaveRepository {

    override fun findAll(): List<Wave> = jdbc.sql("SELECT label, deadline FROM wave")
        .query { rs, _ -> Wave(rs.getString("label"), LocalDate.parse(rs.getString("deadline"))) }
        .list()

    override fun saveAll(waves: List<Wave>) {
        waves.forEach { wave ->
            jdbc.sql("INSERT OR IGNORE INTO wave (label, deadline) VALUES (:label, :deadline)")
                .param("label", wave.label)
                .param("deadline", wave.deadline.toString())
                .update()
        }
    }
}
