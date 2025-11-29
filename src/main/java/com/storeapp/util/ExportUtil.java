package com.storeapp.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.storeapp.model.Sale;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting sales data to CSV and PDF formats
 */
public final class ExportUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    private ExportUtil() {
        // Utility class - no instances
    }

    /**
     * Export sales data to CSV format
     */
    public static void exportToCSV(List<Sale> sales, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV header
            writer.append("Sale ID,Product ID,Product Name,Category,Quantity,Total Price,Sale Date\n");

            // Write data rows
            for (Sale sale : sales) {
                writer.append(String.valueOf(sale.getId())).append(",");
                writer.append(String.valueOf(sale.getProductId())).append(",");
                writer.append(escapeCsv(sale.getProductName())).append(",");
                writer.append(escapeCsv(sale.getCategory())).append(",");
                writer.append(String.valueOf(sale.getQuantity())).append(",");
                writer.append(String.format("%.2f", sale.getTotalPrice())).append(",");
                writer.append(sale.getSaleDate().format(DATE_FORMATTER)).append("\n");
            }

            writer.flush();
        }
    }

    /**
     * Export sales data to PDF format
     */
    public static void exportToPDF(List<Sale> sales, File file) throws IOException {
        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Add title
            Paragraph title = new Paragraph("Sales Report")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Add generation date
            Paragraph date = new Paragraph("Generated: " + java.time.LocalDateTime.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(date);

            // Create table with 7 columns
            float[] columnWidths = {1, 1.5f, 2.5f, 2, 1, 1.5f, 2.5f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths))
                    .useAllAvailableWidth();

            // Add table headers
            String[] headers = {"Sale ID", "Product ID", "Product Name", "Category", "Quantity", "Total Price", "Sale Date"};
            for (String header : headers) {
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(header).setBold())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));
            }

            // Add data rows
            double totalRevenue = 0.0;
            int totalQuantity = 0;

            for (Sale sale : sales) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getId()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getProductId()))));
                table.addCell(new Cell().add(new Paragraph(sale.getProductName() != null ? sale.getProductName() : "N/A")));
                table.addCell(new Cell().add(new Paragraph(sale.getCategory() != null ? sale.getCategory() : "N/A")));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getQuantity()))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.format("$%.2f", sale.getTotalPrice()))).setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(sale.getSaleDate().format(DATE_FORMATTER))));

                totalRevenue += sale.getTotalPrice();
                totalQuantity += sale.getQuantity();
            }

            document.add(table);

            // Add summary
            Paragraph summary = new Paragraph("\n\nSummary:")
                    .setBold()
                    .setFontSize(14)
                    .setMarginTop(20);
            document.add(summary);

            Paragraph stats = new Paragraph(
                    String.format("Total Sales: %d\nTotal Items Sold: %d\nTotal Revenue: $%.2f",
                            sales.size(), totalQuantity, totalRevenue))
                    .setFontSize(12);
            document.add(stats);
        }
    }

    /**
     * Escape special characters in CSV fields
     */
    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
