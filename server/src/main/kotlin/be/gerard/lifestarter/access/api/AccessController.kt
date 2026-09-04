package be.gerard.lifestarter.access.api

import be.gerard.lifestarter.access.domain.AccessService
import be.gerard.lifestarter.registration.domain.Activity
import be.gerard.lifestarter.registration.domain.PersonName
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("access")
@Tag(name = "access", description = "Extra ceremony roles and activity access for known guests")
class AccessController(
    private val access: AccessService,
) {

    @GetMapping("roles")
    @Operation(summary = "Extra roles for a named guest; empty when the guest is not a VIP.")
    fun roles(
        @RequestParam("first-name") @NotBlank firstName: String,
        @RequestParam("last-name") @NotBlank lastName: String,
    ): List<String> = access.findRoles(PersonName(firstName, lastName))

    @GetMapping("activities")
    @Operation(summary = "Activities a party may attend, falling back to what their pledge includes.")
    fun activities(
        @RequestParam(value = "pledge", required = false) pledge: String?,
        @RequestParam("first-first-name") @NotBlank firstFirstName: String,
        @RequestParam("first-last-name") @NotBlank firstLastName: String,
        @RequestParam(value = "second-first-name", required = false) secondFirstName: String?,
        @RequestParam(value = "second-last-name", required = false) secondLastName: String?,
    ): List<Activity> {
        val names = listOfNotNull(
            PersonName.ofNullable(firstFirstName, firstLastName),
            PersonName.ofNullable(secondFirstName, secondLastName),
        )

        return access.findActivities(pledge, names)
    }
}
