package be.gerard.lifestarter.config

import be.gerard.lifestarter.access.domain.BouncerRepository
import be.gerard.lifestarter.access.domain.VipRepository
import be.gerard.lifestarter.access.repository.SqliteBouncerRepository
import be.gerard.lifestarter.access.repository.SqliteVipRepository
import be.gerard.lifestarter.allergy.domain.AllergyRepository
import be.gerard.lifestarter.allergy.repository.SqliteAllergyRepository
import be.gerard.lifestarter.pledge.domain.PledgeRepository
import be.gerard.lifestarter.pledge.repository.SqlitePledgeRepository
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import be.gerard.lifestarter.registration.repository.SqliteRegistrationRepository
import be.gerard.lifestarter.wave.domain.WaveRepository
import be.gerard.lifestarter.wave.repository.SqliteWaveRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.simple.JdbcClient
import javax.sql.DataSource
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories

/**
 * Wires the SQLite adapters — the zero-setup default.
 *
 * The [DataSource] is declared here rather than left to auto-configuration so the database file's
 * parent directory exists before the pool opens its first connection.
 */
@Configuration(proxyBeanMethods = false)
@Profile(PersistenceProfiles.SQLITE)
class SqlitePersistenceConfiguration {

    @Bean(destroyMethod = "close")
    fun sqliteDataSource(properties: PersistenceProperties): DataSource {
        val databaseFile = properties.sqlite.path.absolute()
        databaseFile.parent?.createDirectories()

        log.info("Using SQLite persistence at {}", databaseFile)

        val config = HikariConfig().apply {
            poolName = "lifestarter-sqlite"
            driverClassName = "org.sqlite.JDBC"
            jdbcUrl = "jdbc:sqlite:$databaseFile"
            // SQLite serialises writes anyway; a single connection avoids SQLITE_BUSY entirely.
            maximumPoolSize = 1
            connectionInitSql = "PRAGMA foreign_keys = ON"
        }

        return HikariDataSource(config)
    }

    @Bean
    fun allergyRepository(jdbc: JdbcClient): AllergyRepository = SqliteAllergyRepository(jdbc)

    @Bean
    fun pledgeRepository(jdbc: JdbcClient): PledgeRepository = SqlitePledgeRepository(jdbc)

    @Bean
    fun waveRepository(jdbc: JdbcClient): WaveRepository = SqliteWaveRepository(jdbc)

    @Bean
    fun registrationRepository(jdbc: JdbcClient): RegistrationRepository =
        SqliteRegistrationRepository(jdbc)

    @Bean
    fun vipRepository(jdbc: JdbcClient): VipRepository = SqliteVipRepository(jdbc)

    @Bean
    fun bouncerRepository(jdbc: JdbcClient): BouncerRepository = SqliteBouncerRepository(jdbc)

    private companion object {
        val log = LoggerFactory.getLogger(SqlitePersistenceConfiguration::class.java)
    }
}
