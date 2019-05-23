package com.optimiza.ehope.web.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.entity.BaseHistoricalEntity;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.service.LkpService;

@Component
@Aspect
public class ServiceInterceptor {

	@PersistenceContext
	public EntityManager entityManager;

	private Logger log = LoggerFactory.getLogger(getClass());
	private Map<String, Class<?>> persistedClasses;

	@PostConstruct
	public void init() {
		Set<Class<?>> clazzes = ReflectionUtil.getAllPersistedClasses(entityManager);
		persistedClasses = new HashMap<>();
		for (Class<?> clazz : clazzes) {
			if (StringUtil.isEmpty(clazz.getSimpleName())) {
				continue;
			}
			if (persistedClasses.containsKey(clazz.getSimpleName())) {
				continue;
			}
			persistedClasses.put(clazz.getSimpleName(), clazz);
		}
	}

	@Before("(execution(* com.optimiza.ehope.*.service.*.*(..)) || execution(* com.optimiza.core.*.service.*.*(..))) && "
			+ "!@annotation(com.optimiza.core.common.annotation.InterceptorFree) && !@target(com.optimiza.core.common.annotation.InterceptorFree) && "
			+ "!execution(* com.optimiza.ehope.lis.onboarding.*.*.*(..))")
	public void beforeInterceptor(JoinPoint point) {
		log.info(point + " called...");
		Class<?> entity = getEntity(point);
		if (entity == null) {
			return;
		}
		boolean isBranched = entity.getSuperclass().equals(BaseAuditableBranchedEntity.class)
				|| entity.getSuperclass().equals(BaseHistoricalEntity.class);
		boolean isTenanted = isBranched ? true : entity.getSuperclass().equals(BaseAuditableTenantedEntity.class);
		if (isTenanted) {
			Filter filter = entityManager.unwrap(Session.class).enableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
			filter.setParameter("tenantId", SecurityUtil.getCurrentUser().getTenantId());
			filter.validate();
		}
		if (isBranched) {
			if (SecurityUtil.getCurrentUser().getBranchId() == null) {
				throw new BusinessException("User does not belong to a branch", "userNoBranch", ErrorSeverity.ERROR);
			}
			Filter branchFilter = entityManager.unwrap(Session.class).enableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			branchFilter.setParameter("branchId", SecurityUtil.getCurrentUser().getBranchId());
			branchFilter.validate();
		}

	}

	private Class<?> getEntity(JoinPoint point) {
		Class<?> entity = null;
		if (point.getTarget().getClass().equals(LkpService.class)) {
			for (Object o : point.getArgs()) {
				if (o instanceof Class) {
					entity = (Class<?>) o;
					break;
				} else if (o instanceof BaseEntity) {
					BaseEntity baseEntity = (BaseEntity) o;
					entity = baseEntity.getClass();
					break;
				}
			}
		} else {
			String serviceName = point.getTarget().getClass().getSimpleName();
			serviceName = serviceName.substring(0, serviceName.indexOf("Service"));
			entity = persistedClasses.get(serviceName);
		}
		return entity;
	}

	//	@Pointcut("execution(public !void org.springframework.data.repository.Repository+.*(..))")
	//	public void publicNonVoidRepositoryMethod() {
	//	}
	//
	//	@Before("publicNonVoidRepositoryMethod()")
	//	public void publicNonVoidRepositoryMethod(JoinPoint point) throws Exception {
	//		log.info(point + " publicNonVoidRepositoryMethod...");
	//		Advised adv = (Advised) point.getTarget();
	//		BaseRepositoryImpl o = (BaseRepositoryImpl) adv.getTargetSource().getTarget();
	//		log.info(o.getEntityClass().getSimpleName());
	//		boolean isBranched = o.getEntityClass().getSuperclass().equals(BaseAuditableBranchedEntity.class);
	//		boolean isTenanted = isBranched ? true : o.getEntityClass().getSuperclass().equals(BaseAuditableTenantedEntity.class);
	//		if (isTenanted) {
	//			Filter filter = entityManager.unwrap(Session.class).enableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
	//			filter.setParameter("tenantId", SecurityUtil.getCurrentUser().getTenantId());
	//			filter.validate();
	//		}
	//		if (isBranched) {
	//			if (SecurityUtil.getCurrentUser().getBranchId() == null) {
	//				throw new BusinessException("User does not belong to a branch", "userNoBranch", ErrorSeverity.ERROR);
	//			}
	//			Filter branchFilter = entityManager.unwrap(Session.class).enableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
	//			branchFilter.setParameter("branchId", SecurityUtil.getCurrentUser().getBranchId());
	//			branchFilter.validate();
	//		}
	//	}

}
