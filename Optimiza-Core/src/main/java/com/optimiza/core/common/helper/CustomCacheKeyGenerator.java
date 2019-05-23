package com.optimiza.core.common.helper;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import com.optimiza.core.common.util.SecurityUtil;

/**
 * CustomCacheKeyGenerator.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/12/2017
 **/
@Component
public class CustomCacheKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		return generateKey(params);
	}

	public static String generateKey(Object... params) {
		Class<?> entityClass = null;

		Long tenantId = SecurityUtil.getCurrentUser().getTenantId();
		for (Object obj : params) {
			if (obj instanceof Class<?>) {
				entityClass = (Class<?>) obj;
			}
		}

		if (entityClass == null) {
			throw new IllegalArgumentException("Cannot Find [Class<?>] Parameter");
		} else if (tenantId == null) {
			throw new IllegalArgumentException("Tenant Id Cannot Be NULL");
		}

		StringBuilder sb = new StringBuilder();

		sb.append(entityClass.getSimpleName());
		sb.append("-");
		sb.append(tenantId);

		return sb.toString();
	}

}

//@Override
//@SuppressWarnings("unchecked")
//public Object generate(Object target, Method method, Object... params) {
//
//	Class<?> entityClass = null;
//	List<SearchCriterion> filterList = null;
//	List<OrderObject> orderObjectList = null;
//	Sort sort = null;
//	String[] joins = null;
//
//	for (Object object : params) {
//		if (object instanceof Class<?>) {
//			entityClass = (Class<?>) object;
//		} else if (object instanceof Sort) {
//			sort = (Sort) object;
//		} else if (object instanceof String[]) {
//			joins = (String[]) object;
//		} else if (object instanceof List) {
//			List<?> list = (List<?>) object;
//			if (list.isEmpty()) {
//				continue;
//			}
//			Object firstObj = list.get(0);
//			if (firstObj instanceof SearchCriterion) {
//				filterList = (List<SearchCriterion>) list;
//			} else if (firstObj instanceof OrderObject) {
//				orderObjectList = (List<OrderObject>) list;
//			}
//		}
//	}
//
//	if (entityClass == null) {
//		throw new IllegalArgumentException("Cannot Find [Class<?>] Parameter");
//	}
//
//	Long tenantId = SecurityUtil.getCurrentUser().getTenantId();
//	if (tenantId == null) {
//		throw new IllegalArgumentException("Tenant Id Cannot Be NULL");
//	}
//
//	boolean isEvicting = false;
//	for (Annotation annotation : method.getAnnotations()) {
//		if (annotation instanceof CacheEvict) {
//			isEvicting = true;
//			break;
//		}
//	}
//
//	StringBuilder sb = new StringBuilder();
//
//	sb.append(entityClass.getSimpleName());
//	sb.append(SEPARATOR);
//	sb.append(tenantId);
//	if (!isEvicting) {
//		sb.append(filterList != null ? (SEPARATOR + filterList.hashCode()) : "");
//		sb.append(orderObjectList != null ? (SEPARATOR + orderObjectList.hashCode()) : "");
//		sb.append(sort != null ? (SEPARATOR + sort.hashCode()) : "");
//		sb.append(joins != null ? (SEPARATOR + joins.hashCode()) : "");
//	}
//	return sb.toString();
//}
