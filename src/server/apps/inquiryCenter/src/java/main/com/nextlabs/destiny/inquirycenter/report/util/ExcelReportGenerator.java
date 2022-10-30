package com.nextlabs.destiny.inquirycenter.report.util;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.nextlabs.destiny.inquirycenter.report.birt.datagen.CustomAttributeData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.ObligationLogData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.PADetailsTableData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.PALogDetailsData;

/**
 * Excel Report Generator
 * 
 * @author Amila Silva
 *
 */
public class ExcelReportGenerator {

	private static final Log log = LogFactory
			.getLog(ExcelReportGenerator.class);

	/**
	 * <p>
	 * Generate Tabular Excel Report according to the given header and data
	 * </p>
	 * 
	 * @param headerColumns
	 *            list of header columns
	 * @param dataRows
	 *            data rows
	 * @return ByteArrayOutputStream export report data as a byte Array
	 */
	public static ByteArrayOutputStream generateTableReport(
			List<String> headerColumns, List<List<Object>> dataRows) {

		try (HSSFWorkbook workbook = new HSSFWorkbook()){
			long startTime = System.currentTimeMillis();

			HSSFSheet worksheet = workbook.createSheet("Reporter Data");
			Map<String, CellStyle> styles = createStyles(workbook);

			createHeaderRow(worksheet, 0, styles, headerColumns);
			createDataRow(worksheet, 1, styles, dataRows);
			
			// set column width to auto
			for(int col = 0; col < headerColumns.size(); col++) {
			    worksheet.autoSizeColumn(col);
			}
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			outputStream.flush();

			long endTime = System.currentTimeMillis();

			log.info("Time take to generate Excel Report :"
					+ (endTime - startTime) + " milis");

			return outputStream;
		} catch (Exception e) {
			log.error("Error occurred while generating the report,", e);
			return null;
		}
	}

	/**
	 * <p>
	 * Generate Detail report
	 * </p>
	 * 
	 * @param logDetail
	 *            {@link PALogDetailsData}
	 * @return {@link ByteArrayOutputStream}
	 */
	public static ByteArrayOutputStream generateDetailReport(
			PALogDetailsData logDetail) {

		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			long startTime = System.currentTimeMillis();

			HSSFSheet worksheet = workbook.createSheet("Log Detail Report");
			Map<String, CellStyle> styles = createStyles(workbook);

			CreationHelper createHelper = workbook.getCreationHelper();

			int rowIndex = 1;
			rowIndex = createTitleRow(worksheet, styles, rowIndex);
			rowIndex++;
			rowIndex = createEventDetails(logDetail, worksheet, styles,
					createHelper, rowIndex);
			rowIndex++;
			rowIndex++;
			rowIndex = createCustomAttribs(logDetail, worksheet, styles,
					rowIndex);
			rowIndex++;
			rowIndex++;
			rowIndex = createObligations(logDetail, worksheet, styles, rowIndex);

            // set column width to auto
            for(int col = 0; col < 9; col++) {
                worksheet.autoSizeColumn(col);
            }
            
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			outputStream.flush();

			long endTime = System.currentTimeMillis();

			log.info("Time take to generate Excel Report :"
					+ (endTime - startTime) + " milis");

			return outputStream;
		} catch (Exception e) {
			log.error("Error occurred while generating the report,", e);
			return null;
		}
	}

	private static int createObligations(PALogDetailsData logDetail,
			HSSFSheet worksheet, Map<String, CellStyle> styles, int rowIndex) {

		HSSFRow eventDetailHeaderRow = worksheet.createRow(rowIndex++);
		CellStyle headerStyle = styles.get("header");
		HSSFCell cell = eventDetailHeaderRow.createCell(0);
		cell.setCellValue("Obligation Details");
		cell.setCellStyle(headerStyle);
		worksheet.addMergedRegion(new CellRangeAddress(eventDetailHeaderRow
				.getRowNum(), eventDetailHeaderRow.getRowNum(), 0, 8));

		List<ObligationLogData> obligs = logDetail.getObligationLogData();

		for (ObligationLogData oblig : obligs) {

			HSSFRow dataRow = worksheet.createRow(rowIndex++);
			CellStyle labelStyle = styles.get("label");
			cell = dataRow.createCell(0);
			cell.setCellValue("Obligation Name");
			cell.setCellStyle(labelStyle);

			CellStyle valueStyle = styles.get("value");
			cell = dataRow.createCell(1);
			cell.setCellValue(oblig.getName());
			cell.setCellStyle(valueStyle);
			worksheet.addMergedRegion(new CellRangeAddress(dataRow.getRowNum(),
					dataRow.getRowNum(), 1, 6));

			dataRow = worksheet.createRow(rowIndex++);
			cell = dataRow.createCell(0);
			cell.setCellValue("Discription 1");
			cell.setCellStyle(labelStyle);

			cell = dataRow.createCell(1);
			cell.setCellValue((oblig.getAttributeOne() != null) ? oblig
					.getAttributeOne() : "");
			cell.setCellStyle(valueStyle);
			worksheet.addMergedRegion(new CellRangeAddress(dataRow.getRowNum(),
					dataRow.getRowNum(), 1, 6));

			dataRow = worksheet.createRow(rowIndex++);
			cell = dataRow.createCell(0);
			cell.setCellValue("Discription 2");
			cell.setCellStyle(labelStyle);

			cell = dataRow.createCell(1);
			cell.setCellValue((oblig.getAttributeTwo() != null) ? oblig
					.getAttributeTwo() : "");
			cell.setCellStyle(valueStyle);
			worksheet.addMergedRegion(new CellRangeAddress(dataRow.getRowNum(),
					dataRow.getRowNum(), 1, 6));

			dataRow = worksheet.createRow(rowIndex++);
			cell = dataRow.createCell(0);
			cell.setCellValue("Discription 3");
			cell.setCellStyle(labelStyle);

			cell = dataRow.createCell(1);
			cell.setCellValue((oblig.getAttributeThree() != null) ? oblig
					.getAttributeThree() : "");
			cell.setCellStyle(valueStyle);
			worksheet.addMergedRegion(new CellRangeAddress(dataRow.getRowNum(),
					dataRow.getRowNum(), 1, 6));

			rowIndex++;
		}

		return rowIndex;
	}

	private static int createCustomAttribs(PALogDetailsData logDetail,
			HSSFSheet worksheet, Map<String, CellStyle> styles, int rowIndex) {

		HSSFRow eventDetailHeaderRow = worksheet.createRow(rowIndex++);
		CellStyle headerStyle = styles.get("header");
		HSSFCell cell = eventDetailHeaderRow.createCell(0);
		cell.setCellValue("Custom Attributes");
		cell.setCellStyle(headerStyle);
		worksheet.addMergedRegion(new CellRangeAddress(eventDetailHeaderRow
				.getRowNum(), eventDetailHeaderRow.getRowNum(), 0, 8));

		List<CustomAttributeData> custAttrs = logDetail.getCustAttrData();

		for (CustomAttributeData attrib : custAttrs) {
			if (attrib.getAttributeValue() != null
					&& !attrib.getAttributeValue().isEmpty()) {

				HSSFRow dataRow = worksheet.createRow(rowIndex++);
				CellStyle labelStyle = styles.get("label");
				cell = dataRow.createCell(0);
				cell.setCellValue(attrib.getAttributeName());
				cell.setCellStyle(labelStyle);

				CellStyle valueStyle = styles.get("value");
				cell = dataRow.createCell(1);
				cell.setCellValue(attrib.getAttributeValue());
				cell.setCellStyle(valueStyle);
			}
		}

		return rowIndex;
	}

	private static int createEventDetails(PALogDetailsData logDetail,
			HSSFSheet worksheet, Map<String, CellStyle> styles,
			CreationHelper createHelper, int rowIndex) {

		HSSFRow eventDetailHeaderRow = worksheet.createRow(rowIndex++);
		CellStyle headerStyle = styles.get("header");
		HSSFCell cell = eventDetailHeaderRow.createCell(0);
		cell.setCellValue("Event Details");
		cell.setCellStyle(headerStyle);
		worksheet.addMergedRegion(new CellRangeAddress(eventDetailHeaderRow
				.getRowNum(), eventDetailHeaderRow.getRowNum(), 0, 8));

		PADetailsTableData tableData = logDetail.getSingleLogDetailsData();

		// Event Detail data
		int row = rowIndex;
		HSSFRow dataRow = worksheet.createRow(row);
		CellStyle labelStyle = styles.get("label");
		cell = dataRow.createCell(0);
		cell.setCellValue("Date");
		cell.setCellStyle(labelStyle);

		CellStyle valueStyle = styles.get("value");
		valueStyle.setDataFormat(createHelper.createDataFormat().getFormat(
				"d/m/yyyy h:mm:ss"));
		cell = dataRow.createCell(1);
		cell.setCellValue(new Date(tableData.getTime().getTime()));
		cell.setCellStyle(valueStyle);
		worksheet.addMergedRegion(new CellRangeAddress(dataRow
				.getRowNum(), dataRow.getRowNum(), 1, 2));

		row = ++rowIndex;
		dataRow = worksheet.createRow(row);
		labelStyle = styles.get("label");
		cell = dataRow.createCell(0);
		cell.setCellValue("Policy");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(1);
		cell.setCellValue(tableData.getPolicyFullName());
		cell.setCellStyle(valueStyle);

		labelStyle = styles.get("label");
		cell = dataRow.createCell(3);
		cell.setCellValue("Enforcement");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(4);
		cell.setCellValue(tableData.getPolicyDecision());
		cell.setCellStyle(valueStyle);

		row = ++rowIndex;
		dataRow = worksheet.createRow(row);
		labelStyle = styles.get("label");
		cell = dataRow.createCell(0);
		cell.setCellValue("User");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(1);
		cell.setCellValue(tableData.getUserName());
		cell.setCellStyle(valueStyle);

		labelStyle = styles.get("label");
		cell = dataRow.createCell(3);
		cell.setCellValue("Action");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(4);
		cell.setCellValue(tableData.getAction());
		cell.setCellStyle(valueStyle);

		row = ++rowIndex;
		dataRow = worksheet.createRow(row);
		labelStyle = styles.get("label");
		cell = dataRow.createCell(0);
		cell.setCellValue("From Resource");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(1);
		cell.setCellValue(tableData.getFromResourceName());
		cell.setCellStyle(valueStyle);

		row = ++rowIndex;
		dataRow = worksheet.createRow(row);
		labelStyle = styles.get("label");
		cell = dataRow.createCell(0);
		cell.setCellValue("To Resource");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(1);
		cell.setCellValue(tableData.getToResourceName());
		cell.setCellStyle(valueStyle);

		row = ++rowIndex;
		dataRow = worksheet.createRow(row);
		labelStyle = styles.get("label");
		cell = dataRow.createCell(0);
		cell.setCellValue("Host");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(1);
		cell.setCellValue(tableData.getHostName());
		cell.setCellStyle(valueStyle);

		labelStyle = styles.get("label");
		cell = dataRow.createCell(3);
		cell.setCellValue("Host IP");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(4);
		cell.setCellValue(tableData.getHostIP());
		cell.setCellStyle(valueStyle);

		row = ++rowIndex;
		dataRow = worksheet.createRow(row);
		labelStyle = styles.get("label");
		cell = dataRow.createCell(0);
		cell.setCellValue("Application");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(1);
		cell.setCellValue(tableData.getApplicationName());
		cell.setCellStyle(valueStyle);

		labelStyle = styles.get("label");
		cell = dataRow.createCell(3);
		cell.setCellValue("Event Level");
		cell.setCellStyle(labelStyle);

		valueStyle = styles.get("value");
		cell = dataRow.createCell(4);
		cell.setCellValue("Event Level " + tableData.getLogLevel());
		cell.setCellStyle(valueStyle);

		return rowIndex++;
	}

	private static int createTitleRow(HSSFSheet worksheet,
			Map<String, CellStyle> styles, int rowIndex) {
		HSSFRow titleRow = worksheet.createRow(rowIndex++);
		CellStyle titleStyle = styles.get("title");
		HSSFCell cell = titleRow.createCell(0);
		cell.setCellValue("Log Detail Report");
		cell.setCellStyle(titleStyle);
		worksheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
				titleRow.getRowNum(), 0, 8));
		return rowIndex;
	}

	private static void createHeaderRow(HSSFSheet worksheet, int startingRowNo,
			Map<String, CellStyle> styles, List<String> headerNames) {

		HSSFRow headerRow = worksheet.createRow(startingRowNo);

		CellStyle headerStyle = styles.get("header");

		int index = 0;
		for (String columnName : headerNames) {
			HSSFCell cell = headerRow.createCell(index);
			cell.setCellValue(columnName.toUpperCase());
			cell.setCellStyle(headerStyle);
			index++;
		}
	}

	private static void createDataRow(HSSFSheet worksheet, int startingRowNo,
			Map<String, CellStyle> styles, List<List<Object>> columnData) {

		CellStyle evenCellStyle = styles.get("odd_cell");

		for (int i = 0; i < columnData.size(); i++) {
			HSSFRow dataRow = worksheet.createRow(i + startingRowNo);

			int index = 0;
			for (Object data : columnData.get(i)) {
				HSSFCell cell = dataRow.createCell(index);

				cell.setCellValue(String.valueOf(data)); // consider all as
															// Strings

				if (i % 2 == 1)
					cell.setCellStyle(evenCellStyle);
				index++;
			}
		}
	}

	/**
	 * Create a library of cell styles
	 */
	private static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

		CellStyle style;
		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 18);
		titleFont.setBold(true);
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(titleFont);
		styles.put("title", style);

		Font headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 11);
		headerFont.setColor(IndexedColors.WHITE.getIndex());

		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFont);
		style.setWrapText(true);
		styles.put("header", style);

		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styles.put("odd_cell", style);

		style = wb.createCellStyle();
		Font labelFont = wb.createFont();
		labelFont.setFontHeightInPoints((short) 11);
		labelFont.setBold(true);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setFont(labelFont);
		styles.put("label", style);

		style = wb.createCellStyle();
		Font valueFont = wb.createFont();
		valueFont.setFontHeightInPoints((short) 11);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setFont(valueFont);
		style.setWrapText(true);
		styles.put("value", style);

		return styles;
	}

}
