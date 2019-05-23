package com.optimiza.ehope.lis.util;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.postgresql.util.PSQLException;

import com.optimiza.core.common.util.StringUtil;

public class ExceptionUtil {

	//TODO: IF CHANGED VENDOR THEN RECHECK THESE
	public static final String EXCEPTION_SEPARATOR = ":";

	//https://www.postgresql.org/docs/9.6/static/errcodes-appendix.html
	public static final String STRING_VIOLATION = "22001";
	public static final String NUMBER_VIOLATION = "22003";
	public static final String NOT_NULL_VIOLATION = "23502";
	public static final String FOREIGN_KEY_VIOLATION = "23503";
	public static final String UNIQUE_VIOLATION = "23505";

	public static String handlePSQL(PSQLException psqlException) {

		String value = psqlException.getMessage();
		String columnName = "";
		String message = "";
		try {
			switch (psqlException.getSQLState()) {
				case STRING_VIOLATION:
					message = value.substring(value.lastIndexOf(":") + 1).trim();
					break;
				case NUMBER_VIOLATION:
					message = value.substring(value.indexOf("numeric")).trim();
					break;
				case NOT_NULL_VIOLATION:
					value = value.substring(value.indexOf("\""), value.indexOf("constraint") + 11).trim();
					columnName = value.substring(value.indexOf("\"") + 1, value.lastIndexOf("\"")).trim();
					message = value.substring(value.lastIndexOf("\"") + 1).trim();
					break;
				case FOREIGN_KEY_VIOLATION:
					message = "Violates foreign key constraint";//no need to substring anything since it is a sensitive data
					break;
				case UNIQUE_VIOLATION:
					columnName = value.substring(value.indexOf("(") + 1, value.indexOf(")=(")).trim();
					message = value.substring(value.indexOf(")=(") + 2).trim();
					message = message.replaceAll("\\(|\\)", "");
					break;
			}
		} catch (IndexOutOfBoundsException | NullPointerException e) {//in case the exception message was different
			return "";
		}

		//Only return the message if the column field was not found
		if (StringUtil.isEmpty(columnName)) {
			return message;
		} else {
			return columnName + EXCEPTION_SEPARATOR + message;
		}

	}

	public static List<String> handleConstraintViolations(ConstraintViolationException cve) {
		List<String> values = new ArrayList<>();
		for (ConstraintViolation<?> constraintViolation : cve.getConstraintViolations()) {
			values.add(constraintViolation.getPropertyPath().toString() + EXCEPTION_SEPARATOR + constraintViolation.getMessage());
		}
		return values;
	}
}
