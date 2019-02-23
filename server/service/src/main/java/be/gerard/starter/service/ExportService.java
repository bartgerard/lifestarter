package be.gerard.starter.service;

import be.gerard.starter.config.ExportProperties;
import be.gerard.starter.model.ContactOption;
import be.gerard.starter.model.Guest;
import be.gerard.starter.model.Registration;
import be.gerard.starter.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ExportService
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Service
@RequiredArgsConstructor
public class ExportService {

    private static final String[] GUESTS_HEADERS = new String[]{
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
            "Phone Number",
            "Preferred Contact Method"
    };

    private final RegistrationRepository registrationRepository;
    private final ExportProperties exportProperties;

    public void export() throws IOException {
        final List<Registration> registrations = registrationRepository.findAll();

        try (final Workbook workbook = XSSFWorkbookFactory.createWorkbook()) {
            final CellStyle headerStyle = createHeaderStyle(workbook);

            final Sheet sheet = workbook.createSheet("guests");
            final Row header = sheet.createRow(0);

            for (int i = 0; i < GUESTS_HEADERS.length; i++) {
                final Cell cell = header.createCell(i);
                cell.setCellValue(GUESTS_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            sheet.createFreezePane(0, 1);

            int i = 1;

            for (final Registration registration : registrations) {
                final Optional<ContactOption> contactOption = registration.getContactOptions().stream().findFirst();

                for (final Guest guest : registration.getGuests()) {
                    final Row row = sheet.createRow(i++);
                    row.createCell(0).setCellValue(registration.getPledgeName());
                    row.createCell(2).setCellValue(guest.getFirstName());
                    row.createCell(3).setCellValue(guest.getLastName());
                    row.createCell(4).setCellValue(Objects.toString(guest.getDiet()));

                    if (Objects.nonNull(registration.getActivities())) {
                        row.createCell(5).setCellValue(registration.getActivities().stream()
                                .map(Enum::name)
                                .collect(Collectors.joining(","))
                        );
                    }

                    row.createCell(6).setCellValue(String.join(",", guest.getAllergies()));

                    contactOption.ifPresent(contact -> {
                        row.createCell(1).setCellValue(contact.getEmail());
                        row.createCell(7).setCellValue(contact.getAddress());
                        row.createCell(8).setCellValue(contact.getZipCode());
                        row.createCell(9).setCellValue(contact.getCity());
                        row.createCell(10).setCellValue(contact.getPhoneNumber());
                        row.createCell(11).setCellValue(Objects.toString(contact.getContactMethod()));
                    });
                }
            }

            try (final FileOutputStream fos = new FileOutputStream(exportProperties.getUri() + LocalDate.now() + ".xlsx")) {
                workbook.write(fos);
            }
        }
    }

    private CellStyle createHeaderStyle(
            final Workbook workbook
    ) {
        final Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        final CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);

        return headerStyle;
    }

}
