package be.gerard.lifestarter.export.api

import be.gerard.lifestarter.export.domain.ExportService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("exports")
@Tag(name = "exports", description = "Spreadsheet downloads for the organisers")
class ExportController(
    private val exports: ExportService,
) {
    @GetMapping("registrations", produces = [SPREADSHEET_MEDIA_TYPE])
    @Operation(summary = "Download every RSVP as an .xlsx workbook. Requires the ADMIN role.")
    fun registrations(): ResponseEntity<Resource> {
        val export = exports.exportRegistrations()

        val contentDisposition = ContentDisposition.attachment()
            .filename(export.fileName)
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(MediaType.parseMediaType(export.contentType))
            .contentLength(export.size.toLong())
            .body(ByteArrayResource(export.content))
    }

    private companion object {
        const val SPREADSHEET_MEDIA_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    }
}
