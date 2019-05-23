package com.optimiza.ehope.lis.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.postgresql.util.PSQLException;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.FieldMetaData;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.helper.FieldType;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.ehope.lis.helper.ExcelColumn;
import com.optimiza.ehope.lis.helper.ExcelSheet;

public class ExcelUtil {

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	private static DataFormatter df = new DataFormatter();

	public static Map<String, Object> getModelMap(ExcelSheet excelSheet) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("excelSheet", excelSheet);
		return modelMap;
	}

	public static <T> T createObjectFromRow(Row row, Class<T> clazz, List<ExcelColumn> columns, List<ComTenantLanguage> languages) {
		return createObjectFromRow(row, clazz, columns, languages, null);
	}

	/**
	 * Create an object from excel row.
	 * 
	 * @param row : excel row.
	 * @param clazz : to create obect from.
	 * @param columns : columns used to generate the template.
	 * @param languages : tenant languages.
	 * 
	 * @return T of type clazz
	 */
	public static <T> T createObjectFromRow(Row row, Class<T> clazz, List<ExcelColumn> columns, List<ComTenantLanguage> languages,
			Map<String, Object> transientFields) {
		T obj = null;
		try {
			obj = clazz.newInstance();
			int count = 0;
			for (int idx = 0; idx < columns.size(); idx++) {
				ExcelColumn ec = columns.get(idx);
				if (!ReflectionUtil.doesEntityHaveField(clazz, ec.getOriginalField())) {
					if (transientFields != null) {
						transientFields.put(ec.getOriginalField(), getDataFromCell(row.getCell(count++), String.class));
					}
					continue;
				}
				Field field = clazz.getDeclaredField(ec.getOriginalField());
				field.setAccessible(true);
				Object fieldValue = null;
				if (ec.getType().equals(FieldType.TRANS_FIELD) == false) {
					fieldValue = getDataFromCell(row.getCell(count++), field.getType(), ec.getEntityRidValueMap());
				} else {
					TransField tf = new TransField();
					for (ComTenantLanguage lang : languages) {
						String value = (String) getDataFromCell(row.getCell(count++), field.getType(), null);
						if (StringUtil.isEmpty(value)) {
							continue;
						}
						tf.put(lang.getComLanguage().getLocale(), value);
					}
					if (tf.size() == 0) {
						tf = null;
					}
					fieldValue = tf;
				}
				field.set(obj, fieldValue);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Get a Workbook from MultipartFile.
	 * 
	 * @param file
	 * 
	 * @return Workbook
	 */
	public static Workbook getWorkbookFromExcel(MultipartFile file) {
		Workbook workbook = null;
		try {
			String fileName = file.getOriginalFilename();
			InputStream inputStream = file.getInputStream();
			String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
			if (extension.equals("xls")) {
				workbook = new HSSFWorkbook(inputStream);
			} else {
				workbook = new XSSFWorkbook(inputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workbook;
	}

	public static Object getDataFromCell(Cell cell, Class<?> clazz)
			throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException, ParseException {
		return getDataFromCell(cell, clazz, null);
	}

	/**
	 * Convert a Cell to an Object with the class type.
	 * 
	 * @param cell : row's cell.
	 * @param clazz : to cast the cell to.
	 * 
	 * @return Object
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws ParseException
	 */
	public static Object getDataFromCell(Cell cell, Class<?> clazz, Map<Long, BaseEntity> entityRidValueMap)
			throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException, ParseException {
		if (cell == null) {
			return null;
		}
		Object value = null;
		String stringCellValue = getStringFromCell(cell);
		if (StringUtil.isEmpty(stringCellValue)) {
			return value;
		}
		if (clazz.equals(Long.class)) {
			value = Long.valueOf(stringCellValue);
		} else if (clazz.equals(Date.class)) {
			if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
				value = cell.getDateCellValue();
			} else {
				value = DateUtil.parseDate(stringCellValue, DATE_FORMAT);
			}
		} else if (clazz.equals(Boolean.class)) {
			value = Boolean.valueOf(stringCellValue);
		} else if (clazz.equals(String.class) || clazz.equals(TransField.class)) {
			value = stringCellValue;
		} else {
			Long ridValue = Long.valueOf(stringCellValue.substring(stringCellValue.indexOf("[") + 1, stringCellValue.indexOf("]")));
			value = entityRidValueMap.get(ridValue);
		}
		return value;
	}

	/**
	 * To be used when catching the errors when saving an entity.
	 * Populate the failed rows of the excel sheet
	 * 
	 * @param excelSheet
	 * @param row
	 * @param exception
	 */
	public static void handleExcelExceptions(ExcelSheet excelSheet, Row row, Exception exception) {
		Map<String, String> fields = new HashMap<>();
		Map<String, FieldMetaData> metaData = ReflectionUtil.getEntityFieldMetaData(excelSheet.getEntityType());//so we can map between database columns and field names
		if (exception instanceof ConstraintViolationException) {
			ConstraintViolationException cve = (ConstraintViolationException) exception;
			List<String> values = ExceptionUtil.handleConstraintViolations(cve);
			for (String value : values) {
				String fieldName = value.substring(0, value.indexOf(ExceptionUtil.EXCEPTION_SEPARATOR));
				String message = value.substring(value.indexOf(ExceptionUtil.EXCEPTION_SEPARATOR) + 1);
				fields.put(fieldName, message);
			}
		} else if (exception.getCause() != null && exception.getCause().getCause() != null
				&& exception.getCause().getCause() instanceof PSQLException) {
			String value = ExceptionUtil.handlePSQL((PSQLException) exception.getCause().getCause());
			//We have a column name otherwise we only have a message without a column name so we can show it
			if (!StringUtil.isEmpty(value) && value.indexOf(ExceptionUtil.EXCEPTION_SEPARATOR) != -1) {
				String columnName = value.substring(0, value.indexOf(ExceptionUtil.EXCEPTION_SEPARATOR));
				String message = value.substring(value.indexOf(ExceptionUtil.EXCEPTION_SEPARATOR) + 1);
				for (Map.Entry<String, FieldMetaData> entrySet : metaData.entrySet()) {
					if (entrySet.getValue().getColumnName() != null && entrySet.getValue().getColumnName().equals(columnName)) {
						fields.put(entrySet.getKey(), message);
						break;
					}
				}

			}
		}

		excelSheet.addToFailedRows(row, fields);
	}

	public static boolean isRowEmpty(Row row) {
		for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
			Cell cell = row.getCell(c);
			if (cell != null && cell.getCellTypeEnum() != CellType.BLANK) {
				return false;
			}
		}
		return true;
	}

	public static String getStringFromCell(Cell cell) {
		String stringCellValue = df.formatCellValue(cell).toString();//format everything to a String	
		return stringCellValue.trim();
	}
}
