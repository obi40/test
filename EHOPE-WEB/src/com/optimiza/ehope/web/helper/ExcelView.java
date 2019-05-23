package com.optimiza.ehope.web.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.FieldMetaData;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.helper.FieldType;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.ehope.lis.helper.ExcelColumn;
import com.optimiza.ehope.lis.helper.ExcelSheet;

@Component
public class ExcelView extends AbstractXlsView {

	public static final String DATE_FORMAT = "dd/MM/yyyy";

	@Value("${system.token.defaultExpiration}")
	public String DEFAULT_TOKEN_EXPIRATION;

	@Autowired
	private ComTenantLanguageService languageService;

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<ComTenantLanguage> languages = languageService.findTenantExcelLanguages();
		ComTenantLanguage primaryLanguage = null;
		for (ComTenantLanguage language : languages) {
			if (language.getIsPrimary()) {
				primaryLanguage = language;
				break;
			}
		}
		ExcelSheet rootSheet = (ExcelSheet) model.get("excelSheet");

		response.setHeader("Content-Disposition", "attachment; filename=\"" + rootSheet.getName() + ".xls\"");

		Font headerFont = workbook.createFont();
		headerFont.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		headerFont.setBold(true);
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFont(headerFont);
		HSSFPalette palette = ((HSSFWorkbook) workbook).getCustomPalette();
		//replace lavender with the custom color
		palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.LAVENDER.getIndex(), (byte) 36, (byte) 111, (byte) 156);
		headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LAVENDER.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		CellStyle requirementsStyle = workbook.createCellStyle();
		requirementsStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		requirementsStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		requirementsStyle.setBorderBottom(BorderStyle.DOUBLE);
		requirementsStyle.setWrapText(true);
		requirementsStyle.setVerticalAlignment(VerticalAlignment.TOP);

		Font redFont = workbook.createFont();
		redFont.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		redFont.setBold(true);

		createSheet(workbook, rootSheet, headerStyle, headerFont, requirementsStyle, redFont, primaryLanguage, languages, null);
	}

	private void createSheet(Workbook workbook, ExcelSheet excelSheet, CellStyle headerStyle, Font headerFont,
			CellStyle requirementsStyle, Font redFont, ComTenantLanguage primaryLanguage, List<ComTenantLanguage> languages,
			FieldMetaData identifierFieldMetaData)
			throws Exception {
		Map<String, FieldMetaData> metaData = ReflectionUtil.getEntityFieldMetaData(excelSheet.getEntityType());
		Sheet sheet = workbook.createSheet(excelSheet.getName().replaceAll("\\s+", "_"));
		List<ExcelColumn> columns = excelSheet.getColumns();
		int lovColumnCount = 0;
		Row header = sheet.createRow(0);
		Row requirementsRow = sheet.createRow(1);
		sheet.createFreezePane(0, 2);
		int columnCount = 0;
		if (identifierFieldMetaData != null) {
			Cell stringCell = header.createCell(columnCount, CellType.STRING);
			Cell stringRequirementsCell = requirementsRow.createCell(columnCount, CellType.STRING);
			drawHeader(stringCell, headerStyle, excelSheet.getIdentifierTitle(), primaryLanguage.getIsPrimary(), redFont,
					stringRequirementsCell, requirementsStyle, null, identifierFieldMetaData);
			sheet.autoSizeColumn(columnCount);
			columnCount++;
		}
		for (ExcelColumn column : columns) {
			switch (column.getType()) {
				default:
				case STRING:
					Cell stringCell = header.createCell(columnCount, CellType.STRING);
					Cell stringRequirementsCell = requirementsRow.createCell(columnCount, CellType.STRING);
					Class<?> stringClazz = null;
					FieldMetaData stringFieldMetaData = null;
					if (ReflectionUtil.doesEntityHaveField(excelSheet.getEntityType(), column.getOriginalField())) {
						stringClazz = ReflectionUtil.getFieldType(excelSheet.getEntityType(), column.getOriginalField());
						stringFieldMetaData = metaData.get(column.getOriginalField());
					}
					drawHeader(stringCell, headerStyle, column.getTitle().toUpperCase(), primaryLanguage.getIsPrimary(), redFont,
							stringRequirementsCell, requirementsStyle, stringClazz, stringFieldMetaData);
					sheet.autoSizeColumn(columnCount);
					if (column.getEntityClass() != null) {
						addLovConstraint(workbook, sheet, column, primaryLanguage, lovColumnCount++, columnCount);
					}
					columnCount++;
					break;
				case BOOLEAN:
					Cell booleanCell = header.createCell(columnCount, CellType.STRING);
					Cell booleanRequirementsCell = requirementsRow.createCell(columnCount, CellType.STRING);
					Class<?> booleanClazz = null;
					FieldMetaData booleanFieldMetaData = null;
					if (ReflectionUtil.doesEntityHaveField(excelSheet.getEntityType(), column.getOriginalField())) {
						booleanClazz = ReflectionUtil.getFieldType(excelSheet.getEntityType(), column.getOriginalField());
						booleanFieldMetaData = metaData.get(column.getOriginalField());
					}
					drawHeader(booleanCell, headerStyle, column.getTitle().toUpperCase(), primaryLanguage.getIsPrimary(), redFont,
							booleanRequirementsCell, requirementsStyle, booleanClazz, booleanFieldMetaData);
					sheet.autoSizeColumn(columnCount);
					addLovConstraint(workbook, sheet, column, primaryLanguage, lovColumnCount++, columnCount);
					columnCount++;
					break;
				case TRANS_FIELD:
					for (ComTenantLanguage language : languages) {
						Cell transFieldCell = header.createCell(columnCount, CellType.STRING);
						Cell requirementsCell = requirementsRow.createCell(columnCount, CellType.STRING);
						String transFieldText = (column.getTitle() + " [" + language.getComLanguage().getLocale()).toUpperCase() + "]";
						Class<?> transFieldClazz = null;
						FieldMetaData transFieldMetaData = null;
						if (ReflectionUtil.doesEntityHaveField(excelSheet.getEntityType(), column.getOriginalField())) {
							transFieldClazz = ReflectionUtil.getFieldType(excelSheet.getEntityType(), column.getOriginalField());
							transFieldMetaData = metaData.get(column.getOriginalField());
						}
						drawHeader(transFieldCell, headerStyle, transFieldText, language.getIsPrimary(), redFont,
								requirementsCell, requirementsStyle, transFieldClazz, transFieldMetaData);
						sheet.autoSizeColumn(columnCount);
						columnCount++;
					}
					break;
			}
		}

		if (!CollectionUtil.isMapEmpty(excelSheet.getFailedRows())) {
			fillFailedRows(workbook, sheet, columns, languages, excelSheet.getFailedRows());
		}
		if (!CollectionUtil.isCollectionEmpty(excelSheet.getSubSheets())) {
			for (ExcelSheet subSheet : excelSheet.getSubSheets()) {
				createSheet(workbook, subSheet, headerStyle, headerFont, requirementsStyle, redFont, primaryLanguage, languages,
						metaData.get(subSheet.getIdentifierField()));
			}
		}
	}

	private void addLovConstraint(Workbook workbook, Sheet sheet,
			ExcelColumn column, ComTenantLanguage primaryLanguage,
			int lovColumnCount, int columnCount)
			throws Exception {

		String[] stringList;
		if (column.getType().equals(FieldType.BOOLEAN)) {
			stringList = new String[] { Boolean.TRUE.toString().toUpperCase(), Boolean.FALSE.toString().toUpperCase() };
		} else {
			Field field = column.getEntityClass().getDeclaredField(column.getLabelField());
			field.setAccessible(true);
			List<? extends BaseEntity> values = column.getEntityValues();

			stringList = new String[values.size()];
			for (int i = 0; i < stringList.length; i++) {
				BaseEntity value = values.get(i);
				String displayValue;
				if (field.getType().equals(TransField.class)) {
					TransField transField = (TransField) field.get(value);
					displayValue = transField.get(primaryLanguage.getComLanguage().getLocale());
				} else {
					displayValue = field.get(value).toString();
				}
				stringList[i] = displayValue + " [" + value.getRid() + "]";
			}
		}

		String lovSheetName = sheet.getSheetName() + "_LovSheet";
		Sheet lovSheet = workbook.getSheet(lovSheetName);
		if (lovSheet == null) {
			lovSheet = workbook.createSheet(lovSheetName);
			workbook.setSheetHidden(workbook.getSheetIndex(lovSheet), true);
		}

		Row header = lovSheet.getRow(0);
		if (header == null) {
			header = lovSheet.createRow(0);
		}
		Cell headerCell = header.createCell(lovColumnCount);
		headerCell.setCellValue(column.getTitle());
		for (int j = 0; j < stringList.length; j++) {
			Row row = lovSheet.getRow(j + 1);
			if (row == null) {
				row = lovSheet.createRow(j + 1);
			}
			Cell dataCell = row.createCell(lovColumnCount);
			dataCell.setCellValue(stringList[j]);
		}

		char columnName = 'A';
		int ascii = columnName;
		ascii += lovColumnCount;
		columnName = (char) ascii;

		Name namedCell = workbook.createName();
		namedCell.setNameName(column.getTitle().replaceAll("\\s+", "_"));
		namedCell.setRefersToFormula(
				lovSheetName + "!$" + columnName + "$2:$" + columnName + "$" + (stringList.length + 1));
		DataValidationHelper validationHelper = new HSSFDataValidationHelper((HSSFSheet) sheet);
		DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(namedCell.getNameName());
		CellRangeAddressList addressList = new CellRangeAddressList(1, 65535, columnCount, columnCount);
		DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
		sheet.addValidationData(dataValidation);
	}

	private void drawHeader(Cell headerCell, CellStyle headerStyle, String title,
			boolean isPrimary, Font redFont,
			Cell requirementsCell, CellStyle requirementsStyle, Class<?> fieldType, FieldMetaData metaData) {
		headerCell.setCellStyle(headerStyle);
		RichTextString richString;
		if (isPrimary && metaData != null && metaData.isNotNull()) {
			title += "*";
			richString = new HSSFRichTextString(title);
			richString.applyFont(title.length() - 1, title.length(), redFont);
		} else {
			richString = new HSSFRichTextString(title);
		}
		headerCell.setCellValue(richString);
		requirementsCell.setCellStyle(requirementsStyle);
		List<String> requirementsList = new ArrayList<String>();
		if (metaData != null && metaData.isSized()) {
			requirementsList.add("Min: " + metaData.getMin());
			requirementsList.add("Max: " + metaData.getMax());
		}
		if (metaData != null && metaData.isEmail()) {
			requirementsList.add("Email: example@domain.com");
		}
		if (fieldType != null) {
			if (fieldType.equals(Date.class)) {
				requirementsList.add(DATE_FORMAT);
			}
		}
		requirementsCell.setCellValue(String.join("\n", requirementsList));
	}

	public void fillFailedRows(Workbook workbook, Sheet sheet, List<ExcelColumn> columns, List<ComTenantLanguage> languages,
			LinkedHashMap<Row, Map<String, String>> failedRows) {
		CellStyle errorStyle = workbook.createCellStyle();//style for comments
		errorStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
		errorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		CellStyle dateStyle = workbook.createCellStyle();//date styles so we can show the date value
		CreationHelper createHelper = workbook.getCreationHelper();
		dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(DATE_FORMAT));
		int rowCounter = 2;
		int cellCounter = 0;
		for (Map.Entry<Row, Map<String, String>> entrySet : failedRows.entrySet()) {
			Row failedRow = entrySet.getKey();
			Map<String, String> failedRowErrors = entrySet.getValue();
			Row newRow = sheet.createRow(rowCounter++);//start from 3rd row
			for (int index = 0; index < columns.size(); index++) {
				ExcelColumn ec = columns.get(index);
				String message = failedRowErrors.get(ec.getOriginalField());//can be null which means no errors on this field
				if (ec.getType().equals(FieldType.TRANS_FIELD) == false) {
					addFailedData(newRow, failedRow.getCell(cellCounter++), cellCounter, message, errorStyle, dateStyle);
				} else {
					for (int i = 0; i < languages.size(); i++) {
						addFailedData(newRow, failedRow.getCell(cellCounter++), cellCounter, message, errorStyle, dateStyle);
					}
				}

			}
			cellCounter = 0;//reset
		}
	}

	private void addFailedData(Row newRow, Cell cell, Integer cellCounter, String message, CellStyle errorStyle, CellStyle dateStyle) {
		Cell newCell = null;
		String nullValue = null;//so we can insert a null value in the cell, does not accept .setCellValue(null)
		if (cell == null) {//means this cell is empty so create a cell with null so we can put comments
			newCell = newRow.createCell(cellCounter - 1);//minus one since we already increment it
			newCell.setCellValue(nullValue);
		} else {
			newCell = newRow.createCell(cell.getColumnIndex(), cell.getCellTypeEnum());
			if (cell.getCellTypeEnum() != null) {
				switch (cell.getCellTypeEnum()) {
					case BOOLEAN:
						newCell.setCellValue(cell.getBooleanCellValue());
						break;
					case NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							newCell.setCellValue(cell.getDateCellValue());
							newCell.setCellStyle(dateStyle);
						} else {
							newCell.setCellValue(cell.getNumericCellValue());
						}
						break;
					case STRING:
						newCell.setCellValue(cell.getStringCellValue());
						break;
					default:
						newCell.setCellValue(nullValue);
						break;

				}
			}
		}
		createComment(newCell, errorStyle, message);

	}

	public void createComment(Cell cell, CellStyle style, String message) {
		if (cell == null || style == null || StringUtil.isEmpty(message)) {
			return;
		}
		Drawing<?> drawing = cell.getSheet().createDrawingPatriarch();
		CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
		// When the comment box is visible, have it show in a 1x3 space
		ClientAnchor anchor = factory.createClientAnchor();
		anchor.setCol1(cell.getColumnIndex());
		anchor.setCol2(cell.getColumnIndex() + 1);
		anchor.setRow1(cell.getRowIndex());
		anchor.setRow2(cell.getRowIndex() + 1);
		anchor.setDx1(100);
		anchor.setDx2(1000);
		anchor.setDy1(100);
		anchor.setDy2(1000);
		// Create the comment and set the text+author
		Comment comment = drawing.createCellComment(anchor);
		RichTextString str = factory.createRichTextString(message);
		comment.setString(str);
		// Assign the comment to the cell
		cell.setCellComment(comment);
		if (style != null) {
			cell.setCellStyle(style);
		}

	}
}
