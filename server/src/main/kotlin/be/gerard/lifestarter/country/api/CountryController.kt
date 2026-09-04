package be.gerard.lifestarter.country.api

import be.gerard.lifestarter.country.domain.CountryService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

/** ISO country, named in the language negotiated through `Accept-Language`. */
data class CountryTo(
    val iso2: String,
    val iso3: String,
    val name: String,
)

@RestController
@RequestMapping("countries")
@Tag(name = "countries", description = "ISO country list for the address form")
class CountryController(
    private val countries: CountryService,
) {

    @GetMapping
    fun countries(locale: Locale): List<CountryTo> = countries.findAll(locale)
        .map { CountryTo(it.iso2, it.iso3, it.name) }
}
