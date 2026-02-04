package com.blueharbor.hotel.service;

import com.blueharbor.hotel.config.ReportingProperties;
import com.blueharbor.hotel.model.feedback.Feedback;
import com.blueharbor.hotel.model.report.ActivityRow;
import com.blueharbor.hotel.model.report.OccupancyRow;
import com.blueharbor.hotel.model.report.RevenueRow;
import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private final ReportingProperties reportingProperties;

    public ExportService(ReportingProperties reportingProperties) {
        this.reportingProperties = reportingProperties;
    }

    public Path exportRevenueCsv(List<RevenueRow> rows) {
        Path file = ensureExportFolder().resolve("revenue.csv");
        writeCsv(file, writer -> {
            writer.writeNext(new String[]{"Period", "Reservations", "Subtotal", "Tax", "Discounts", "Total"});
            rows.forEach(row -> writer.writeNext(new String[]{
                row.periodLabel(),
                String.valueOf(row.reservations()),
                row.subtotal().toString(),
                row.tax().toString(),
                row.discounts().toString(),
                row.total().toString()
            }));
        });
        return file;
    }

    public Path exportOccupancyCsv(List<OccupancyRow> rows) {
        Path file = ensureExportFolder().resolve("occupancy.csv");
        writeCsv(file, writer -> {
            writer.writeNext(new String[]{"Date", "Rooms Available", "Rooms Occupied", "Occupancy %"});
            rows.forEach(row -> writer.writeNext(new String[]{
                row.date().toString(),
                String.valueOf(row.roomsAvailable()),
                String.valueOf(row.roomsOccupied()),
                String.valueOf(row.occupancyPercentage())
            }));
        });
        return file;
    }

    public Path exportActivityTxt(List<ActivityRow> rows) {
        Path file = ensureExportFolder().resolve("activity.txt");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            StringBuilder builder = new StringBuilder();
            for (ActivityRow row : rows) {
                builder.append(formatter.format(row.timestamp()))
                    .append(" | ")
                    .append(row.actor())
                    .append(" | ")
                    .append(row.action())
                    .append(" | ")
                    .append(row.message())
                    .append(System.lineSeparator());
            }
            Files.writeString(file, builder.toString());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write TXT export", e);
        }
        return file;
    }

    public Path exportActivityCsv(List<ActivityRow> rows) {
        Path file = ensureExportFolder().resolve("activity.csv");
        writeCsv(file, writer -> {
            writer.writeNext(new String[]{"Timestamp", "Actor", "Action", "Entity", "Identifier", "Message"});
            rows.forEach(row -> writer.writeNext(new String[]{
                row.timestamp().toString(),
                row.actor(),
                row.action(),
                row.entityType(),
                row.entityIdentifier(),
                row.message()
            }));
        });
        return file;
    }

    public Path exportRevenuePdf(List<RevenueRow> rows) {
        Path file = ensureExportFolder().resolve("revenue.pdf");
        writePdf(file, "Revenue Report", rows.stream()
            .map(row -> row.periodLabel() + " : " + row.total())
            .toList());
        return file;
    }

    public Path exportOccupancyPdf(List<OccupancyRow> rows) {
        Path file = ensureExportFolder().resolve("occupancy.pdf");
        writePdf(file, "Occupancy Report", rows.stream()
            .map(row -> row.date() + " : " + row.occupancyPercentage() + "%")
            .toList());
        return file;
    }

    public Path exportFeedbackCsv(List<Feedback> feedback) {
        Path file = ensureExportFolder().resolve("feedback.csv");
        writeCsv(file, writer -> {
            writer.writeNext(new String[]{"Guest", "Rating", "Comment", "Date"});
            feedback.forEach(entry -> writer.writeNext(new String[]{
                entry.getReservation().getGuest().getFirstName(),
                String.valueOf(entry.getRating()),
                entry.getComment(),
                entry.getSubmittedAt().toString()
            }));
        });
        return file;
    }

    private void writeCsv(Path file, CsvWriterConsumer consumer) {
        try {
            Files.createDirectories(file.getParent());
            try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(file))) {
                consumer.accept(writer);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write CSV export", e);
        }
    }

    private void writePdf(Path file, String title, List<String> lines) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText(title);
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                float y = 720;
                for (String line : lines) {
                    contentStream.newLineAtOffset(0, -30);
                    contentStream.showText(line);
                    y -= 16;
                }
                contentStream.endText();
            }
            document.save(file.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write PDF export", e);
        }
    }

    private Path ensureExportFolder() {
        Path folder = Path.of(reportingProperties.getExportFolder());
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create export folder", e);
        }
        return folder;
    }

    @FunctionalInterface
    interface CsvWriterConsumer {
        void accept(CSVWriter writer) throws IOException;
    }
}
