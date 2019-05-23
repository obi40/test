package com.optimiza.core.lkp.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optimiza.core.admin.helper.AdminRights;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.lkp.model.LkpGender;

/**
 * LkpService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/27/2017
 **/
@Service("LkpService")
@Transactional(readOnly = false)
@SuppressWarnings("unchecked")
public class LkpService {

	@Autowired
	private EntityManager entityManager;

	/**
	 * Validate if the incoming entity is a Lkp
	 * 
	 * @param clazz
	 */
	public void isLkp(Class<?> clazz) {
		if (!clazz.getSimpleName().startsWith("Lkp")) {
			throw new BusinessException("Class is not a Lkp", "invalidParameters", ErrorSeverity.ERROR);
		}
	}

	/**
	 * Check wither the clazz is tenant or not , also if it is a Lkp
	 * 
	 * @param clazz
	 * @return true if the clazz extends BaseAuditableTenantedEntity
	 */
	public boolean isLkpTenanted(Class<?> clazz) {
		isLkp(clazz);
		return (clazz.getSuperclass() == BaseAuditableTenantedEntity.class);
	}

	/**
	 * Custom behaviour for some lkps.
	 * 
	 * @param clazz
	 * @param entity
	 */
	public void lkpsValidation(Class<?> clazz, BaseEntity entity) {
		if (clazz == LkpGender.class) {
			LkpGender newGender = (LkpGender) entity;
			//If male or female then throw exception
			if (newGender.getRid() != null) {
				LkpGender oldGender = findOneAnyLkp(Arrays.asList(new SearchCriterion("rid", newGender.getRid(), FilterOperator.eq)),
						clazz);
				if (oldGender.getCode().equals("MALE") || oldGender.getCode().equals("FEMALE")) {
					throw new BusinessException("Can't modify this lkp", "cantModifyLkp", ErrorSeverity.ERROR);
				}
			}
		}

	}

	/**
	 * Find one Lkp entity
	 * 
	 * @param searchCriterionList
	 * @param clazz
	 * @param joins
	 * @return object of clazz
	 */
	public <T extends BaseEntity> T findOneAnyLkp(List<SearchCriterion> searchCriterionList, Class<?> clazz, String... joins) {
		isLkp(clazz);
		Class<BaseEntity> entityClass = (Class<BaseEntity>) clazz;
		return (T) ReflectionUtil.getRepository(entityClass.getSimpleName()).findOne(searchCriterionList, entityClass, joins);
	}

	/**
	 * Fetch Lkp list without cache
	 * 
	 * @param searchCriterionList
	 * @param clazz
	 * @param sort : nullable
	 * @param joins
	 * @return List of clazz
	 */
	public <T extends BaseEntity> List<T> findAnyLkp(List<SearchCriterion> searchCriterionList, Class<?> clazz, Sort sort,
			String... joins) {
		isLkp(clazz);
		Class<BaseEntity> entityClass = (Class<BaseEntity>) clazz;
		if (sort == null) {//default
			sort = new Sort(new Order(Direction.ASC, "code"));
		}
		return (List<T>) ReflectionUtil.getRepository(entityClass.getSimpleName()).find(searchCriterionList, entityClass, sort,
				joins);
	}

	/**
	 * Get All Lkps that extends the parameter clazz.
	 * 
	 * @param clazz
	 * @return Set
	 */
	public List<Class<?>> getLkpsByScope(Class<?> clazz) {
		return ReflectionUtil	.getAllPersistedClasses(entityManager).stream()
								.filter(c -> c.getSuperclass().equals(clazz)
										&& c.getSimpleName().startsWith("Lkp"))
								.distinct()
								.collect(Collectors.toList());
	}

	@CacheEvict(cacheNames = "LKPs", allEntries = false, key = "#entity.getClass().getSimpleName()")
	public BaseEntity createNonTenantedLkp(BaseEntity entity) {
		SecurityUtil.authorizeApplicationAdmin();
		return ReflectionUtil.getRepository(entity.getClass().getSimpleName()).save(entity);
	}

	@CacheEvict(cacheNames = "LKPs", allEntries = false, key = "#entity.getClass().getSimpleName()")
	public BaseEntity updateNonTenantedLkp(BaseEntity entity) {
		SecurityUtil.authorizeApplicationAdmin();
		return ReflectionUtil.getRepository(entity.getClass().getSimpleName()).save(entity);
	}

	@CacheEvict(cacheNames = "LKPs", allEntries = false, key = "#entity.getClass().getSimpleName()")
	public void deleteNonTenantedLkp(BaseEntity entity) {
		SecurityUtil.authorizeApplicationAdmin();
		ReflectionUtil.getRepository(entity.getClass().getSimpleName()).delete(entity);
	}

	@Cacheable(cacheNames = "LKPs", key = "#entityClass.getSimpleName()")
	public <T extends BaseEntity> List<T> findNonTenantedLkp(List<SearchCriterion> filters, Class<?> entityClass, Sort sort,
			String... join) {
		Class<BaseEntity> baseClass = (Class<BaseEntity>) entityClass;
		if (sort == null) {//default
			sort = new Sort(new Order(Direction.ASC, "code"));
		}
		return (List<T>) ReflectionUtil.getRepository(entityClass.getSimpleName()).find(filters, baseClass, sort, join);
	}

	@PreAuthorize("hasAuthority('" + AdminRights.ADD_LKP + "')")
	@CacheEvict(cacheNames = "LKPs", allEntries = false, keyGenerator = "customCacheKeyGenerator")
	public BaseEntity createTenantedLkp(Class<?> entityClass, BaseEntity entity) {
		lkpsValidation(entityClass, entity);
		return ReflectionUtil.getRepository(entity.getClass().getSimpleName()).save(entity);
	}

	@PreAuthorize("hasAuthority('" + AdminRights.UPD_LKP + "')")
	@CacheEvict(cacheNames = "LKPs", allEntries = false, keyGenerator = "customCacheKeyGenerator")
	public BaseEntity updateTenantedLkp(Class<?> entityClass, BaseEntity entity) {
		lkpsValidation(entityClass, entity);
		return ReflectionUtil.getRepository(entity.getClass().getSimpleName()).save(entity);
	}

	@PreAuthorize("hasAuthority('" + AdminRights.DEL_LKP + "')")
	@CacheEvict(cacheNames = "LKPs", allEntries = false, keyGenerator = "customCacheKeyGenerator")
	public void deleteTenantedLkp(Class<?> entityClass, BaseEntity entity) {
		lkpsValidation(entityClass, entity);
		ReflectionUtil.getRepository(entity.getClass().getSimpleName()).delete(entity);
	}

	@Cacheable(cacheNames = "LKPs", keyGenerator = "customCacheKeyGenerator")
	public <T extends BaseEntity> List<T> findTenantedLkp(List<SearchCriterion> filters, Class<?> entityClass,
			Sort sort, String... join) {
		Class<BaseEntity> baseClass = (Class<BaseEntity>) entityClass;
		if (sort == null) {//default
			sort = new Sort(new Order(Direction.ASC, "code"));
		}
		return (List<T>) ReflectionUtil.getRepository(entityClass.getSimpleName()).find(filters, baseClass, sort, join);
	}

}
