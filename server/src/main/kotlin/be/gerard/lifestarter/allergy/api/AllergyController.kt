package be.gerard.lifestarter.allergy.api

import be.gerard.lifestarter.allergy.domain.Allergy
import be.gerard.lifestarter.allergy.domain.AllergyService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("allergies")
@Tag(name = "allergies", description = "Allergies a guest can declare")
class AllergyController(
    private val allergies: AllergyService,
) {

    @GetMapping
    fun allergies(): List<String> = allergies.findAll().map(Allergy::id)
}
