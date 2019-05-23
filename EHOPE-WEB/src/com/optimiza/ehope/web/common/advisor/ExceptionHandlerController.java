package com.optimiza.ehope.web.common.advisor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.tomcat.util.http.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.SizeLimitExceededException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.ehope.lis.util.ExceptionUtil;

@ControllerAdvice
public class ExceptionHandlerController {

	@ExceptionHandler({ MissingServletRequestParameterException.class,
			UnsatisfiedServletRequestParameterException.class,
			HttpRequestMethodNotSupportedException.class,
			ServletRequestBindingException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Map<String, Object> handleRequestException(Exception ex) {
		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Request Error");
		map.put("cause", ex.getMessage());
		map.put("code", "requestException");
		return map;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Map<String, Object> handleValidationException(MethodArgumentNotValidException ex) {
		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Validation Failure");
		map.put("violations", convertConstraintViolation(ex));
		map.put("code", "methodArgumentNotValidException");
		return map;
	}

	@ExceptionHandler(ObjectRetrievalFailureException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody Map<String, Object> handleValidationException(ObjectRetrievalFailureException ex) {
		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Entity Not Found");
		map.put("cause", ex.getMessage());
		map.put("code", "objectRetrievalFailureException");
		return map;
	}

	@ExceptionHandler({ DataIntegrityViolationException.class, ConstraintViolationException.class })
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public @ResponseBody Map<String, Object> handleDataIntegrityViolationException(Exception ex) {
		ex.printStackTrace();
		String errorCode = "dataIntegrityViolation";//default
		String cause = ex.getCause() != null ? ex.getCause().toString() : "";
		if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause() instanceof PSQLException) {
			PSQLException pe = (PSQLException) ex.getCause().getCause();
			if (pe.getSQLState().equals(ExceptionUtil.FOREIGN_KEY_VIOLATION)) {
				errorCode = "foreignKeyViolation";
			} else {
				cause = ExceptionUtil.handlePSQL(pe);
			}
		} else if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException cve = (ConstraintViolationException) ex;
			cause = ExceptionUtil.handleConstraintViolations(cve).toString();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Data Integrity Error");
		map.put("cause", cause);
		map.put("code", errorCode);
		return map;
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public @ResponseBody Map<String, Object> handleUnsupportedMediaTypeException(HttpMediaTypeNotSupportedException ex) {
		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Unsupported Media Type");
		map.put("cause", ex.getLocalizedMessage());
		map.put("supported", ex.getSupportedMediaTypes());
		map.put("code", "httpMediaTypeNotSupportedException");
		return map;
	}

	@ExceptionHandler({ OptimisticLockingFailureException.class, ObjectOptimisticLockingFailureException.class,
			ConcurrencyFailureException.class })
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Map<String, Object> handleOptimisticLockingFailureException(ConcurrencyFailureException ex) {
		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Optimistic Locking Failure Exception");
		if (ex.getCause() != null) {
			map.put("cause", ex.getCause().getMessage());
		} else {
			map.put("cause", ex.getMessage());
		}
		map.put("severity", ErrorSeverity.INFO);
		map.put("code", "tryAgain");
		return map;
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public @ResponseBody Map<String, Object> handleAccessDeniedException(AccessDeniedException ex) {
		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Access is Denied");
		if (ex.getCause() != null) {
			map.put("cause", ex.getCause().getMessage());
		} else {
			map.put("cause", ex.getMessage());
		}
		map.put("severity", ErrorSeverity.ERRORFATAL);
		map.put("code", "accessDenied");
		return map;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Map<String, Object> handleUncaughtException(Exception ex) {
		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Unknown Error");
		map.put("code", "somethingWrong");
		if (ex.getCause() != null) {
			map.put("cause", ex.getCause().getMessage());
		} else {
			map.put("cause", ex.getMessage());
		}
		map.put("severity", ErrorSeverity.ERROR);
		return map;
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Map<String, Object> handleUncaughtException(AuthenticationException ex) {
		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Authentication Exception");
		if (ex.getCause() != null) {
			map.put("code", ex.getCause().getMessage());
		} else {
			map.put("code", ex.getMessage());
		}
		map.put("cause", "Authentication Exception");
		map.put("severity", ErrorSeverity.ERROR);
		return map;
	}

	@ExceptionHandler(MultipartException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Map<String, Object> handleMultipartException(MultipartException ex) {
		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "Multipart Exception");
		map.put("cause", ex.getMessage());
		map.put("code", "multipartException");
		if (ex.getCause() != null) {
			Throwable cause = ex.getCause();
			if (cause.getClass().equals(IllegalStateException.class)) {
				cause = cause.getCause();
				if (cause.getClass().equals(FileSizeLimitExceededException.class)) {
					map.put("error", "File Size Limit Exceeded");
					map.put("cause", cause.getMessage());
					map.put("code", "fileSizeLimitExceeded");
				} else if (cause.getClass().equals(SizeLimitExceededException.class)) {
					//TODO this error is not being sent to the client, check it
					map.put("error", "Size Limit Exceeded");
					map.put("cause", cause.getMessage());
					map.put("code", "sizeLimitExceeded");
				}
			}
		}
		return map;
	}

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Map<String, Object> handleBusinessException(BusinessException ex) {

		ex.printStackTrace();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", "BusinessException");
		map.put("code", ex.getCode());
		map.put("parameters", ex.getParameterMap());
		map.put("severity", ex.getSeverity());
		map.put("isVisible", ex.getIsVisible());
		return map;
	}

	private Map<String, Map<String, Object>> convertConstraintViolation(Set<ConstraintViolation<?>> constraintViolations) {
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			Map<String, Object> violationMap = new HashMap<String, Object>();
			violationMap.put("value", constraintViolation.getInvalidValue());
			violationMap.put("type", constraintViolation.getRootBeanClass());
			violationMap.put("message", constraintViolation.getMessage());
			result.put(constraintViolation.getPropertyPath().toString(), violationMap);
		}
		return result;
	}

	private Map<String, Map<String, Object>> convertConstraintViolation(MethodArgumentNotValidException ex) {
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			Map<String, Object> violationMap = new HashMap<String, Object>();
			violationMap.put("target", ex.getBindingResult().getTarget());
			violationMap.put("type", ex.getBindingResult().getTarget().getClass());
			violationMap.put("message", error.getDefaultMessage());
			result.put(error.getObjectName(), violationMap);
		}
		return result;
	}

}