package be.gerard.lifestarter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Path

/** Which persistence adapter backs the domain's outbound repository ports. */
enum class PersistenceType(val profile: String) {
    SQLITE(PersistenceProfiles.SQLITE),
    MONGODB(PersistenceProfiles.MONGODB),
}

/** Profile names that mirror [PersistenceType]; kept as constants so `@Profile` stays type-safe-ish. */
object PersistenceProfiles {
    const val SQLITE = "sqlite"
    const val MONGODB = "mongodb"
}

/**
 * The single knob that decides where data lives.
 *
 * `application.yml` turns [type] into the matching Spring profile through
 * `spring.profiles.include`, so setting `lifestarter.persistence.type=mongodb` (property,
 * `LIFESTARTER_PERSISTENCE_TYPE` env var or `--lifestarter.persistence.type=…`) is all it takes.
 */
@ConfigurationProperties(prefix = "lifestarter.persistence")
data class PersistenceProperties(
    val type: PersistenceType = PersistenceType.SQLITE,
    val sqlite: Sqlite = Sqlite(),
) {
    data class Sqlite(
        /** Location of the SQLite database file; parent directories are created on startup. */
        val path: Path = Path.of("data", "lifestarter.db"),
    )
}
