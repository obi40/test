package com.optimiza.core.base.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.helper.CustomPageRequest;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.repo.BaseRepository;

/**
 * CrudService.java, Super Generic class that is responsible for all CRUD operations
 *
 *
 * @param <ID> Entity ID Type
 * @param <E> Entity Class
 * @param <T> EntityRepository interface
 */

@Transactional(readOnly = false)
public abstract class BaseService<ID extends Serializable, E extends BaseEntity, T extends BaseRepository<E, ID>> {

	/**
	 *
	 * @return Repository Object
	 */
	protected abstract T getRepository();

	@Autowired
	private CustomPageRequest pageable;

	/**
	 * Get an entity by its ID
	 *
	 * @param id
	 * @return
	 */
	public E findById(ID id) {
		return getRepository().findOne(id);
	}

	/**
	 * Get all records
	 *
	 * @return List of records
	 */
	public List<E> findAll() {
		return getRepository().findAll();
	}

	/**
	 * Get records which comply with the filters limited by the pagination information
	 *
	 * @param filters The WHERE conditions to apply to the find query
	 * @param pageable Pagianation information
	 * @param entityClass The class type to return
	 * @param junctionOperator The operator: And/Or
	 * @param join Optional joins
	 * @return Sublist of entities
	 */
	public Page<E> find(List<SearchCriterion> filters, Pageable pageable, Class<E> entityClass, String... join) {
		return getRepository().find(filters, pageable, entityClass, join);
	}

	/**
	 * Get all records which comply with the filters
	 *
	 * @param filters The WHERE conditions to apply to the find query
	 * @param entityClass The class type to return
	 * @param junctionOperator The operator: And/Or
	 * @param join Optional joins
	 * @return List of entities
	 */
	public List<E> find(List<SearchCriterion> filters, Class<E> entityClass, String... join) {
		return getRepository().find(filters, entityClass, join);
	}

	/**
	 * Get all records which comply with the filters
	 *
	 * @param filters The WHERE conditions to apply to the find query
	 * @param entityClass The class type to return
	 * @param junctionOperator The operator: And/Or
	 * @param sortList Sort orders
	 * @param join Optional joins
	 * @return List of entities
	 */
	public List<E> find(List<SearchCriterion> filters, Class<E> entityClass, Sort sort,
			String... join) {
		return getRepository().find(filters, entityClass, sort, join);
	}

	/**
	 * Get a single record which complies with the filters
	 *
	 * @param filters The WHERE conditions to apply to the find query
	 * @param entityClass The class type to return
	 * @param junctionOperator The operator: And/Or
	 * @param join Optional joins
	 * @return Single entity
	 */
	public E findOne(List<SearchCriterion> filters, Class<E> entityClass, String... join) {
		return getRepository().findOne(filters, entityClass, join);
	}

	/**
	 * Get chunk of records
	 *
	 * @param first (start index)
	 * @param pageSize (Number of records)
	 * @return List of entities
	 */
	public Page<E> findAllPages(Pageable pageable) {
		return getRepository().findAll(pageable);
	}

	/**
	 * find All with default paging size
	 *
	 * @return Page<Entity>
	 */
	public Page<E> findAllPages() {
		return getRepository().findAll(this.pageable);
	}

	public Slice<E> findAllSlices(Pageable pageable) {
		return getRepository().findAll(pageable);
	}

	public Slice<E> findAllSlices() {
		return getRepository().findAll(this.pageable);
	}

	/**
	 * get the number of records
	 *
	 * @return long
	 */
	public long count() {
		return getRepository().count();
	}

	/**
	 * check if the generic entity has detail records
	 *
	 * @param rid
	 * @return
	 */
	public boolean isParentRecord(ID rid) {
		return getRepository().isParentRecord(rid);
	}

}
