package be.gerard.lifestarter.export.adapter

import be.gerard.lifestarter.access.domain.Bouncer
import be.gerard.lifestarter.export.domain.RegistrationExporter
import be.gerard.lifestarter.registration.domain.Registration
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream

/** Renders the export as an `.xlsx` workbook, entirely in memory. */
@Component
class ExcelRegistrationExporter : RegistrationExporter {

    override fun render(registrations: List<Registration>, bouncers: List<Bouncer>): ByteArray =
        XSSFWorkbook().use { workbook ->
            val headerStyle = workbook.headerStyle()

            workbook.createSheet("guests").writeGuests(registrations, headerStyle)
            workbook.createSheet("activities").writeActivities(bouncers, headerStyle)

            ByteArrayOutputStream().use { output ->
                workbook.write(output)
                output.toByteArray()
            }
        }

    private fun Sheet.writeGuests(registrations: List<Registration>, headerStyle: CellStyle) {
        writeHeader(GUEST_HEADERS, headerStyle)

        var rowNumber = 1

        registrations.forEach { registration ->
            val contact = registration.contactOptions.firstOrNull()

            registration.guests.forEach { guest ->
                createRow(rowNumber++).apply {
                    write(0, registration.pledgeName)
                    write(1, contact?.email)
                    write(2, guest.firstName)
                    write(3, guest.lastName)
                    write(4, guest.diet?.name)
                    write(5, registration.activities.joinToString(",") { it.name })
                    write(6, guest.allergies.joinToString(","))
                    write(7, contact?.address)
                    write(8, contact?.zipCode)
                    write(9, contact?.city)
                    write(10, contact?.countryIso3)
                    write(11, contact?.phoneNumber)
                    write(12, contact?.contactMethod?.name)
                    write(13, guest.role)
                    write(14, guest.comment)
                }
            }
        }
    }

    private fun Sheet.writeActivities(bouncers: List<Bouncer>, headerStyle: CellStyle) {
        writeHeader(ACTIVITY_HEADERS, headerStyle)

        bouncers.forEachIndexed { index, bouncer ->
            createRow(index + 1).apply {
                write(0, bouncer.name.firstName)
                write(1, bouncer.name.lastName)
                write(2, bouncer.activities.joinToString(",") { it.name })
            }
        }
    }

    private fun Sheet.writeHeader(headers: List<String>, style: CellStyle) {
        val header = createRow(0)

        headers.forEachIndexed { index, title ->
            header.createCell(index).apply {
                setCellValue(title)
                cellStyle = style
            }
        }

        createFreezePane(0, 1)
    }

    private fun Row.write(column: Int, value: String?) {
        createCell(column).setCellValue(value.orEmpty())
    }

    private fun Workbook.headerStyle(): CellStyle {
        val bold = createFont().apply { setBold(true) }
        return createCellStyle().apply { setFont(bold) }
    }

    private companion object {
        val GUEST_HEADERS = listOf(
            "Pledge",
            "Email",
            "First Name",
            "Last Name",
            "Diet",
            "Activities",
            "Allergies",
            "Address",
            "ZipCode",
            "City",
            "Country",
            "Phone Number",
            "Preferred Contact Method",
            "Role",
            "Comment",
        )

        val ACTIVITY_HEADERS = listOf("First Name", "Last Name", "Activities")
    }
}
