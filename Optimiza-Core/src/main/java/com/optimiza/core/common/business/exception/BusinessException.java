package com.optimiza.core.common.business.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BusinessException.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/20/2017
 * 
 * @param stackTraceError : For console logging.
 * @param code : For UI Messages.
 * @param parameterMap : This list will be converted to a map which contains a parameter with a replacement to it. i.e. 'User {1} is disabled' -> Map<'1','55'> ->'User 55 is disabled',
 *            this list should add its values in order as the errorCode requires.
 * @param severity : For UI toast coloring.
 * @param isVisible : Is the UI toast visible?
 * 
 **/
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String code;
	private ErrorSeverity severity;
	private Boolean isVisible;
	private Map<String, String> parameterMap;

	public BusinessException() {
		super();
	}

	// General Use
	public BusinessException(String stackTraceError) {
		super(stackTraceError);
	}

	private BusinessException(String stackTraceError, String code, ErrorSeverity severity, Boolean isVisible) {
		super(stackTraceError);
		this.code = code;
		this.severity = severity;
		this.isVisible = isVisible;
	}

	public BusinessException(String stackTraceError, String code, ErrorSeverity severity) {
		this(stackTraceError, code, severity, Boolean.TRUE);
	}

	public BusinessException(String stackTraceError, String code, ErrorSeverity severity, List<String> parameterList) {
		this(stackTraceError, code, severity, Boolean.TRUE);
		this.parameterMap = createParameterMap(parameterList);
	}

	public BusinessException(String stackTraceError, String code, ErrorSeverity severity, List<String> parameterList, Boolean isVisible) {
		this(stackTraceError, code, severity, isVisible);
		this.parameterMap = createParameterMap(parameterList);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ErrorSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(ErrorSeverity severity) {
		this.severity = severity;
	}

	public Boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, String> parameterMap) {
		this.parameterMap = parameterMap;
	}

	private Map<String, String> createParameterMap(List<String> parameterList) {
		Map<String, String> parameterMap = new HashMap<>();
		for (int i = 0; i < parameterList.size(); i++) {
			parameterMap.put("{" + i + "}", parameterList.get(i));
		}
		return parameterMap;
	}

	public static enum ErrorSeverity {
		INFO("INFO"),
		WARNING("WARNING"),
		ERROR("ERROR"),
		ERRORFATAL("ERRORFATAL");

		private ErrorSeverity(String value) {
			this.value = value;
		}

		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
