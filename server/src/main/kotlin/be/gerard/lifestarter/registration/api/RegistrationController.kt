package be.gerard.lifestarter.registration.api

import be.gerard.lifestarter.registration.domain.Diet
import be.gerard.lifestarter.registration.domain.RegistrationService
import be.gerard.lifestarter.registration.mapper.RegistrationMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("registrations")
@Tag(name = "registrations", description = "Filing and inspecting RSVPs")
class RegistrationController(
    private val registrations: RegistrationService,
    private val mapper: RegistrationMapper,
) {

    @PostMapping
    @Operation(summary = "File an RSVP. Re-posting the same e-mail address replaces the previous one.")
    fun register(
        @Valid @RequestBody request: NewRegistrationTo,
        uriBuilder: UriComponentsBuilder,
    ): ResponseEntity<RegistrationTo> {
        val registration = registrations.register(mapper.toCommand(request))

        val location: URI = uriBuilder.path("/api/registrations/{email}")
            .build(registration.email)

        return ResponseEntity.created(location).body(mapper.toTo(registration))
    }

    @GetMapping("statistics")
    @Operation(summary = "Aggregated guest counts, as shown on the public RSVP page.")
    fun statistics(): RegistrationStatisticsTo = mapper.toTo(registrations.statistics())
}

@RestController
@RequestMapping("diets")
@Tag(name = "diets", description = "Dietary preferences a guest can pick")
class DietController {

    @GetMapping
    fun diets(): List<Diet> = Diet.entries
}
