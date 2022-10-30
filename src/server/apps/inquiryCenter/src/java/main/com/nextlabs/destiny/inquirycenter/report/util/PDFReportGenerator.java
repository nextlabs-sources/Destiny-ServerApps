/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.report.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.CustomAttributeData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.ObligationLogData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.PADetailsTableData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.PALogDetailsData;

/**
 * PDF Report Generator
 * 
 * @author Amila Silva
 *
 */
public class PDFReportGenerator {

    private static final Log log = LogFactory.getLog(PDFReportGenerator.class);

    private final static Color SKY_BLUE_PDF_TABLE_HEADER = new Color(135, 206,
            250);
    final static Color LIGHT_BLUE_PDF_TABLE_HEADER = new Color(204, 225, 255);
    final static Color BLUE_LABEL_TEXT = new Color(0, 38, 153);
    private final static Color EVEN_ROW = new Color(255, 255, 255);
    private final static Color ODD_ROW = new Color(248, 248, 255);
    
    private static Font TITLE_BOLD, BOLD, LABEL_BOLD, NORMAL;

    /**
     * <p>
     * Generate Tabular PDF Report according to the given header and data
     * </p>
     * 
     * 
     * @param headerNames
     *            list of header columns
     * @param dataRows
     *            data rows
     * @return
     * @throws ByteArrayOutputStream
     *             export report data as a byte Array
     */
    public static ByteArrayOutputStream generateTableReport(
            List<String> headerNames, List<List<Object>> dataRows) {

        try {
            long startTime = System.currentTimeMillis();

            BOLD = new Font(BaseFont.createFont(
                    BaseFont.TIMES_ROMAN, "", false), 8.0f, Font.BOLD);
            NORMAL = new Font(BaseFont.createFont(
                    BaseFont.TIMES_ROMAN, "", false), 8.0f, Font.NORMAL);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document iTextPdf = new Document(PageSize.A4, 10, 10, 15, 10);
            PdfWriter.getInstance(iTextPdf, outputStream);
            iTextPdf.open();

            PdfPTable table = generateTable(headerNames, dataRows);

            iTextPdf.add(table);
            iTextPdf.close();

            long endTime = System.currentTimeMillis();

            log.info("Time take to create PDF :" + (endTime - startTime)
                    + " milis");

            return outputStream;
        } catch (Exception e) {
            log.error("Error occurred while generating the PDF report,", e);
            return null;
        }
    }

    
    /**
     * <p>
     * Generate Detail PDF Report according to the given header and data
     * </p>
     * 
     * 
     * @param headerNames
     *            list of header columns
     * @param dataRows
     *            data rows
     * @return
     * @throws ByteArrayOutputStream
     *             export report data as a byte Array
     */
    public static ByteArrayOutputStream generateDetailReport(
            PALogDetailsData logDetail) {

        try {
            long startTime = System.currentTimeMillis();

            TITLE_BOLD = new Font(BaseFont.createFont(
                    BaseFont.TIMES_ROMAN, "", false), 10.0f, Font.BOLD);

            BOLD = new Font(BaseFont.createFont(
                    BaseFont.TIMES_ROMAN, "", false), 8.0f, Font.BOLD);
            
            LABEL_BOLD = new Font(BaseFont.createFont(
                    BaseFont.TIMES_ROMAN, "", false), 8.0f, Font.BOLD);
            LABEL_BOLD.setColor(Color.BLUE);
            
            NORMAL = new Font(BaseFont.createFont(
                    BaseFont.TIMES_ROMAN, "", false), 8.0f, Font.NORMAL);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document iTextPdf = new Document(PageSize.A4, 10, 10, 15, 10);
            PdfWriter.getInstance(iTextPdf, outputStream);
            iTextPdf.open();

            PdfPTable documentTable = createDocumentTable();
            documentTitle(documentTable);
            PdfPTable eventDetailTable = createEventDetailTable(logDetail);
            documentTable.addCell(createCellNoBorder(eventDetailTable, 1));
            documentTable.addCell(emptyCellNoBorder(1));
            
            PdfPTable cusAttribTable = createCustomAttributesTable(logDetail);
            documentTable.addCell(createCellNoBorder(cusAttribTable, 1));
            documentTable.addCell(emptyCellNoBorder(1));
            
            PdfPTable obligationTable = createObligationTable(logDetail);
            documentTable.addCell(createCellNoBorder(obligationTable, 1));
            documentTable.addCell(emptyCellNoBorder(1));

            iTextPdf.add(documentTable);
            iTextPdf.close();

            long endTime = System.currentTimeMillis();

            log.info("Time take to create PDF :" + (endTime - startTime)
                    + " milis");

            return outputStream;
        } catch (Exception e) {
            log.error("Error occurred while generating the PDF report,", e);
            return null;
        }
    }

    private static PdfPTable createObligationTable(PALogDetailsData logDetail) {
        
        PdfPTable detailTable = new PdfPTable(2);
        detailTable.setWidthPercentage(100);
        detailTable.setSpacingBefore(5);
        detailTable.setSpacingAfter(5);

        PdfPCell titleCell = new PdfPCell(
                new Phrase("Obligation Details", BOLD));
        titleCell.setColspan(2);
        titleCell.setBackgroundColor(LIGHT_BLUE_PDF_TABLE_HEADER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(titleCell);
        
        List<ObligationLogData> obligations = logDetail.getObligationLogData();
        for (ObligationLogData oblig : obligations) {
                PdfPTable field = fieldTable(LABEL_BOLD, NORMAL,
                        "   Obligation Name:",
                        oblig.getName());
                detailTable.addCell(createCellNoBorder(field, 1));
                detailTable.addCell(emptyCellNoBorder(1));
                
                field = fieldTable(LABEL_BOLD, NORMAL,
                        "   Discription 1:",
                        (oblig.getAttributeOne() != null) ? oblig.getAttributeOne(): "");
                detailTable.addCell(createCellNoBorder(field, 1));
                detailTable.addCell(emptyCellNoBorder(1));
                
                field = fieldTable(LABEL_BOLD, NORMAL,
                        "   Discription 2:",
                        (oblig.getAttributeTwo() != null) ? oblig.getAttributeTwo(): "");
                detailTable.addCell(createCellNoBorder(field, 1));
                detailTable.addCell(emptyCellNoBorder(1));
                
                field = fieldTable(LABEL_BOLD, NORMAL,
                        "   Discription 3:",
                        (oblig.getAttributeThree() != null) ? oblig.getAttributeThree(): "");
                detailTable.addCell(createCellNoBorder(field, 1));
                detailTable.addCell(emptyCellNoBorder(1));
                
                detailTable.addCell(emptyCellWithBorder(2));
            }
        
        return detailTable;
    }

    private static PdfPTable createCustomAttributesTable(PALogDetailsData logDetail) {
        
        PdfPTable detailTable = new PdfPTable(2);
        detailTable.setWidthPercentage(100);
        detailTable.setSpacingBefore(5);
        detailTable.setSpacingAfter(5);

        PdfPCell titleCell = new PdfPCell(
                new Phrase("Custom Attributes", BOLD));
        titleCell.setColspan(2);
        titleCell.setBackgroundColor(LIGHT_BLUE_PDF_TABLE_HEADER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(titleCell);
        
        List<CustomAttributeData> cusAttributes = logDetail.getCustAttrData();
        int count = 0;
        for (CustomAttributeData cusAttrib : cusAttributes) {
            if (cusAttrib.getAttributeValue() != null
                    && !cusAttrib.getAttributeValue().isEmpty()) {
                PdfPTable field = fieldTable(LABEL_BOLD, NORMAL,
                        "   " + cusAttrib.getAttributeName().toUpperCase() + ":",
                        cusAttrib.getAttributeValue());
                detailTable.addCell(createCellNoBorder(field, 1));
                count++;
            }
        }
        if(count % 2 == 1)
         detailTable.addCell(emptyCellNoBorder(1));

        return detailTable;
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    private static PdfPTable createEventDetailTable(PALogDetailsData logDetail) throws Exception {
        
        PdfPTable eventDetailTable = new PdfPTable(2);
        eventDetailTable.setWidthPercentage(100);
        eventDetailTable.setSpacingBefore(5);
        eventDetailTable.setSpacingAfter(5);

        PdfPCell eventTitleCell = new PdfPCell(
                new Phrase("Event Details", BOLD));
        eventTitleCell.setColspan(2);
        eventTitleCell.setBackgroundColor(LIGHT_BLUE_PDF_TABLE_HEADER);
        eventTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        eventDetailTable.addCell(eventTitleCell);

        PADetailsTableData tableData = logDetail.getSingleLogDetailsData();
        PdfPTable field = fieldTable(LABEL_BOLD, NORMAL, "   Date:", sdf.format(new Date(tableData.getTime().getTime())));
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        eventDetailTable.addCell(emptyCellNoBorder(1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   Policy:", tableData.getPolicyFullName());
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   Enforcement:", tableData.getPolicyDecision());
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   User:", tableData.getUserName());
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   Action:", tableData.getAction());
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   From Resource:", tableData.getFromResourceName());
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        eventDetailTable.addCell(emptyCellNoBorder(1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   To Resource:", tableData.getToResourceName());
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        eventDetailTable.addCell(emptyCellNoBorder(1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   Host:", tableData.getHostName());
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   Host IP:", tableData.getHostIP());
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   Application:", tableData.getApplicationName());
        eventDetailTable.addCell(createCellNoBorder(field, 1));
        field = fieldTable(LABEL_BOLD, NORMAL, "   Event Level:", "Event Level  " + tableData.getLogLevel());
        eventDetailTable.addCell(createCellNoBorder(field, 1));

        return eventDetailTable;
    }

    private static PdfPCell createCellNoBorder(PdfPTable field, int colSpan) {
        PdfPCell cell = new PdfPCell(field);
        cell.setColspan(colSpan);
        cell.setBorder(0);
        return cell;
    }

    private static PdfPTable fieldTable(final Font labelFont, final Font valueFont,
            String label, String value) {
        
        PdfPTable field = new PdfPTable(2);
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        labelCell.setBorder(0);
        valueCell.setBorder(0);
        field.addCell(labelCell);
        field.addCell(valueCell);

        return field;
    }

    private static void documentTitle(PdfPTable documentTable) {
        PdfPCell titleCell = new PdfPCell(new Phrase(
                "Log Detail Report\n     ",  TITLE_BOLD));
        titleCell.setBackgroundColor(SKY_BLUE_PDF_TABLE_HEADER);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setBorder(0);
        documentTable.addCell(titleCell);
    }
    
    private static PdfPCell emptyCellWithBorder(int colSpan) {
        PdfPCell cell = new PdfPCell(Phrase.getInstance(""));
        cell.setColspan(colSpan);
        cell.setBorder(1);
        return cell;
    }

    private static PdfPCell emptyCellNoBorder(int colSpan) {
        PdfPCell cell = new PdfPCell(Phrase.getInstance(""));
        cell.setColspan(colSpan);
        cell.setBorder(0);
        return cell;
    }
    
    private static PdfPTable createDocumentTable() {
        PdfPTable documentTable = new PdfPTable(1);
        documentTable.setWidthPercentage(98);
        return documentTable;
    }

    private static PdfPTable generateTable(List<String> headerNames,
            List<List<Object>> dataRows) {
        PdfPTable table = new PdfPTable(headerNames.size());
        table.setWidthPercentage(98);
        table.setSpacingBefore(5);
        table.setSpacingAfter(5);

        PdfPCell tableCell;

        // header rows
        for (String header : headerNames) {
            tableCell = new PdfPCell(new Phrase(header.toUpperCase(), BOLD));
            tableCell.setBackgroundColor(SKY_BLUE_PDF_TABLE_HEADER);
            tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(tableCell);
        }

        int count = 0;
        // Data rows
        for (List<Object> dataRow : dataRows) {

            for (Object cellVal : dataRow) {
                tableCell = new PdfPCell(new Phrase(String.valueOf(cellVal),
                        NORMAL));

                if (count % 2 == 1) {
                    tableCell.setBackgroundColor(ODD_ROW);
                } else {
                    tableCell.setBackgroundColor(EVEN_ROW);
                }

                tableCell.setHorizontalAlignment(Element.ALIGN_LEFT
                        | Element.ALIGN_JUSTIFIED);
                table.addCell(tableCell);
            }
            count++;
        }
        return table;
    }
}
