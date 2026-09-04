package be.gerard.lifestarter.wave.api

import be.gerard.lifestarter.wave.domain.WaveService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

data class WaveTo(
    val label: String,
    val deadline: LocalDate,
)

@RestController
@RequestMapping("waves")
@Tag(name = "waves", description = "RSVP rounds and their deadlines")
class WaveController(
    private val waves: WaveService,
) {

    @GetMapping
    fun waves(): List<WaveTo> = waves.findAll().map { WaveTo(it.label, it.deadline) }

    @GetMapping("current")
    @Operation(summary = "The next wave still accepting replies; 404 once every deadline has passed.")
    fun currentWave(): ResponseEntity<WaveTo> = waves.currentWave()
        ?.let { ResponseEntity.ok(WaveTo(it.label, it.deadline)) }
        ?: ResponseEntity.notFound().build()
}
