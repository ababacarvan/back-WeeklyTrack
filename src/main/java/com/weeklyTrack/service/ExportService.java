package com.weeklyTrack.service;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.weeklyTrack.dto.WeeklyReportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportService {

    public byte[] exportToExcel(List<WeeklyReportDTO> reports) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Weekly Reports");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Developer", "Week", "Year", "Highlights", "Completed Tasks",
                    "Planned Tasks", "Blockers", "Comments", "Status"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(getHeaderStyle(workbook));
            }

            // Fill data
            int rowNum = 1;
            for (WeeklyReportDTO report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getUserFullName());
                row.createCell(1).setCellValue(report.getWeekNumber());
                row.createCell(2).setCellValue(report.getYear());
                row.createCell(3).setCellValue(report.getHighlights());
                row.createCell(4).setCellValue(report.getCompletedTasks());
                row.createCell(5).setCellValue(report.getPlannedTasks());
                row.createCell(6).setCellValue(report.getBlockers());
                row.createCell(7).setCellValue(report.getAdditionalComments());
                row.createCell(8).setCellValue(report.getStatus().toString());
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel file", e);
        }
    }

    public byte[] exportToPdf(List<WeeklyReportDTO> reports) {
        try {
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Weekly Reports", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Create table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            // Add headers
            addTableHeader(table, "Developer");
            addTableHeader(table, "Week");
            addTableHeader(table, "Year");
            addTableHeader(table, "Highlights");
            addTableHeader(table, "Status");

            // Add data
            for (WeeklyReportDTO report : reports) {
                table.addCell(report.getUserFullName());
                table.addCell(String.valueOf(report.getWeekNumber()));
                table.addCell(String.valueOf(report.getYear()));
                table.addCell(report.getHighlights());
                table.addCell(report.getStatus().toString());
            }

            document.add(table);
            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF file", e);
        }
    }

    private CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = (Font) workbook.createFont();
        ((org.apache.poi.ss.usermodel.Font) font).setBold(true);
        style.setFont((org.apache.poi.ss.usermodel.Font) font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Phrase(header));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }
}
