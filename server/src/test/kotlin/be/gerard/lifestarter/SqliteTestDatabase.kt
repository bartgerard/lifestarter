package be.gerard.lifestarter

import org.springframework.test.context.DynamicPropertyRegistry
import java.nio.file.Files
import java.nio.file.Path

/**
 * Points the SQLite adapter at a throwaway database file.
 *
 * Every test class that boots the context gets its own file, so accumulated registrations from an
 * earlier run can never make an assertion flap.
 */
object SqliteTestDatabase {

    const val ADMIN_USERNAME = "admin"
    const val ADMIN_PASSWORD = "test-secret"

    private val root: Path = Files.createTempDirectory("lifestarter-test")

    fun registerFor(name: String, registry: DynamicPropertyRegistry) {
        val file = root.resolve("$name-${System.nanoTime()}.db")

        registry.add("lifestarter.persistence.type") { "sqlite" }
        registry.add("lifestarter.persistence.sqlite.path") { file.toAbsolutePath().toString() }
        registry.add("lifestarter.security.admin.password") { ADMIN_PASSWORD }
    }
}

/** Convenience for the common `@DynamicPropertySource` boilerplate. */
fun DynamicPropertyRegistry.useThrowawaySqlite(name: String) =
    SqliteTestDatabase.registerFor(name, this)
