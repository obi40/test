package com.optimiza.core.base.repo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.helper.SearchCriterion;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Serializable> extends JpaRepository<T, ID> {

	public boolean hasRecords(Map<String, Object> parameters);

	public boolean hasRecords(Map<String, Object> parameters, Map<String, Object> execludeParameters);

	public boolean isParentRecord(ID rid);

	@SuppressWarnings("unchecked")
	public boolean isParentRecordExclude(ID rid, Class<? extends BaseEntity>... excludeEntities);

	public boolean isRelationLoaded(BaseEntity entity, String attributeName);

	public boolean isEntityPersisted(BaseEntity entity);

	public List<T> find(Map<String, Object> parameters);

	public List<T> find(Map<String, Object> parameters, Map<String, List<? extends BaseEntity>> inParameters,
			Map<String, Object> execludeParameters);

	public List<? extends BaseEntity> find(Class<? extends BaseEntity> entityClass, Map<String, Object> parameters,
			Map<String, List<? extends BaseEntity>> inParameters,
			Map<String, Object> execludeParameters);

	public Page<T> find(List<SearchCriterion> filters, Pageable pageable, Class<T> entityClass, String... join);

	public T findOne(List<SearchCriterion> filters, Class<T> entityClass, String... join);

	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, String... join);

	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, Sort sort, String... join);

}
