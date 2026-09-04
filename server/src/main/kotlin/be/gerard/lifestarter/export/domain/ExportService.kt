package be.gerard.lifestarter.export.domain

import be.gerard.lifestarter.access.domain.BouncerRepository
import be.gerard.lifestarter.registration.domain.RegistrationRepository
import java.time.Clock
import java.time.LocalDate

class ExportService(
    private val registrations: RegistrationRepository,
    private val bouncers: BouncerRepository,
    private val exporter: RegistrationExporter,
    private val clock: Clock,
) {
    fun exportRegistrations(): RegistrationExport {
        val content = exporter.render(registrations.findAll(), bouncers.findAll())

        return RegistrationExport(
            fileName = "lifestarter-registrations-${LocalDate.now(clock)}.xlsx",
            contentType = XLSX_CONTENT_TYPE,
            content = content,
        )
    }

    companion object {
        const val XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    }
}
