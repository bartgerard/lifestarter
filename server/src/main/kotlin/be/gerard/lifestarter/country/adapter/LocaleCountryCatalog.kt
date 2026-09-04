package be.gerard.lifestarter.country.adapter

import be.gerard.lifestarter.country.domain.Country
import be.gerard.lifestarter.country.domain.CountryCatalog
import org.springframework.stereotype.Component
import java.util.Locale

/** Derives the country list from the JDK's ISO data, translated into the caller's language. */
@Component
class LocaleCountryCatalog : CountryCatalog {

    override fun findAll(locale: Locale): List<Country> = Locale.getISOCountries()
        .map { iso2 -> Locale.of("", iso2) }
        .map { country ->
            Country(
                iso2 = country.country,
                iso3 = country.isO3Country,
                name = country.getDisplayCountry(locale),
            )
        }
}
