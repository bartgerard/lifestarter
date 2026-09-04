package be.gerard.lifestarter.country.domain

import java.util.Locale

/** An ISO country, named in the requester's language. */
data class Country(
    val iso2: String,
    val iso3: String,
    val name: String,
)

/** Outbound port for the country list; the JDK happens to know one, but that is an adapter detail. */
fun interface CountryCatalog {
    fun findAll(locale: Locale): List<Country>
}

class CountryService(
    private val catalog: CountryCatalog,
) {
    fun findAll(locale: Locale): List<Country> =
        catalog.findAll(locale).sortedBy { it.name.lowercase(locale) }
}
