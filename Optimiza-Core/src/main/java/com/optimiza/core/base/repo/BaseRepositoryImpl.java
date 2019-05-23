package com.optimiza.core.base.repo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.type.TextType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.helper.JoinWrapper;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.util.ReflectionUtil;

public class BaseRepositoryImpl<T extends BaseEntity, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements BaseRepository<T, ID> {

	private final EntityManager entityManager;

	private Class<T> entityClass;

	public BaseRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) throws ClassNotFoundException {
		super(entityInformation, entityManager);

		this.entityManager = entityManager;
		this.entityClass = entityInformation.getJavaType();
	}

	@Override
	public boolean hasRecords(Map<String, Object> parameters) {
		return hasRecords(parameters, null);
	}

	@Override
	public boolean hasRecords(Map<String, Object> parameters, Map<String, Object> execludeParameters) {

		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();

		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<T> queryRoot = criteriaQuery.from(entityClass);
		criteriaQuery.select(criteriaBuilder.literal(1));

		Predicate predicate = criteriaBuilder.conjunction();
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				Predicate newPredicate;
				if (entry.getValue() == null) {
					newPredicate = criteriaBuilder.isNull(queryRoot.get(entry.getKey()));
				} else {
					newPredicate = criteriaBuilder.equal(queryRoot.get(entry.getKey()), entry.getValue());
				}
				predicate = criteriaBuilder.and(predicate, newPredicate);
			}
		}

		if (execludeParameters != null) {
			for (Entry<String, Object> entry : execludeParameters.entrySet()) {
				Predicate newPredicate;
				if (entry.getValue() == null) {
					newPredicate = criteriaBuilder.isNotNull(queryRoot.get(entry.getKey()));
				} else {
					newPredicate = criteriaBuilder.notEqual(queryRoot.get(entry.getKey()), entry.getValue());
				}
				predicate = criteriaBuilder.and(predicate, newPredicate);
			}
		}

		criteriaQuery.where(predicate);

		TypedQuery<Integer> query = getEntityManager().createQuery(criteriaQuery);

		return query.getResultList().size() > 0;

	}

	@Override
	public List<T> find(Map<String, Object> parameters, Map<String, List<? extends BaseEntity>> inParameters,
			Map<String, Object> execludeParameters) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();

		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> queryRoot = criteriaQuery.from(entityClass);

		Predicate predicate = criteriaBuilder.conjunction();
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				Predicate newPredicate;
				if (entry.getValue() == null) {
					newPredicate = criteriaBuilder.isNull(queryRoot.get(entry.getKey()));
				} else {

					newPredicate = criteriaBuilder.equal(queryRoot.get(entry.getKey()), entry.getValue());

				}
				predicate = criteriaBuilder.and(predicate, newPredicate);
			}
		}

		if (inParameters != null) {
			for (Entry<String, List<? extends BaseEntity>> entry : inParameters.entrySet()) {
				Predicate newPredicate = null;
				if (!entry.getValue().isEmpty()) {

					Expression<String> exp = queryRoot.get(entry.getKey());
					newPredicate = exp.in(entry.getValue());
					predicate = criteriaBuilder.and(predicate, newPredicate);
				}

			}
		}

		if (execludeParameters != null) {
			for (Entry<String, Object> entry : execludeParameters.entrySet()) {
				Predicate newPredicate;
				if (entry.getValue() == null) {
					newPredicate = criteriaBuilder.isNotNull(queryRoot.get(entry.getKey()));
				} else {
					newPredicate = criteriaBuilder.notEqual(queryRoot.get(entry.getKey()), entry.getValue());
				}
				predicate = criteriaBuilder.and(predicate, newPredicate);
			}
		}

		criteriaQuery.where(predicate);

		TypedQuery<T> query = getEntityManager().createQuery(criteriaQuery);

		return query.getResultList();
	}

	@Override
	public List<? extends BaseEntity> find(Class<? extends BaseEntity> entityClass, Map<String, Object> parameters,
			Map<String, List<? extends BaseEntity>> inParameters,
			Map<String, Object> execludeParameters) {

		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();

		CriteriaQuery<? extends BaseEntity> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<? extends BaseEntity> queryRoot = criteriaQuery.from(entityClass);

		Predicate predicate = criteriaBuilder.conjunction();
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				Predicate newPredicate;
				if (entry.getValue() == null) {
					newPredicate = criteriaBuilder.isNull(queryRoot.get(entry.getKey()));
				} else {

					newPredicate = criteriaBuilder.equal(queryRoot.get(entry.getKey()), entry.getValue());

				}
				predicate = criteriaBuilder.and(predicate, newPredicate);
			}
		}

		if (inParameters != null) {
			for (Entry<String, List<? extends BaseEntity>> entry : inParameters.entrySet()) {
				Predicate newPredicate = null;
				if (!entry.getValue().isEmpty()) {

					Expression<String> exp = queryRoot.get(entry.getKey());
					newPredicate = exp.in(entry.getValue());
					predicate = criteriaBuilder.and(predicate, newPredicate);
				}

			}
		}

		if (execludeParameters != null) {
			for (Entry<String, Object> entry : execludeParameters.entrySet()) {
				Predicate newPredicate;
				if (entry.getValue() == null) {
					newPredicate = criteriaBuilder.isNotNull(queryRoot.get(entry.getKey()));
				} else {
					newPredicate = criteriaBuilder.notEqual(queryRoot.get(entry.getKey()), entry.getValue());
				}
				predicate = criteriaBuilder.and(predicate, newPredicate);
			}
		}

		criteriaQuery.where(predicate);

		TypedQuery<? extends BaseEntity> query = getEntityManager().createQuery(criteriaQuery);

		return query.getResultList();

	}

	@Override
	public List<T> find(Map<String, Object> parameters) {

		return find(parameters, null, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isParentRecord(ID rid) {
		return isParentRecordExclude(rid, new Class[0]);

		//		Field[] declaredFields = entityClass.getDeclaredFields();
		//		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		//		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		//		Predicate predicate = criteriaBuilder.conjunction();
		//		criteriaQuery.select(criteriaBuilder.literal(1));
		//
		//		for (int i = 0; i < declaredFields.length; i++) {
		//			Field field = declaredFields[i];
		//			if (field.getType().equals(List.class)) {
		//				if (field.isAnnotationPresent(OneToMany.class)) {
		//					// get type argument, which is the entity that references current entity
		//					@SuppressWarnings("unchecked")
		//					Class<BaseEntity> actualArgument = (Class<BaseEntity>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
		//					// get field name
		//					String mappedBy = field.getAnnotation(OneToMany.class).mappedBy();
		//
		//					Root<BaseEntity> queryRoot = criteriaQuery.from(actualArgument);
		//
		//					Predicate newPredicate = criteriaBuilder.equal(queryRoot.get(mappedBy), rid);
		//					predicate = criteriaBuilder.and(predicate, newPredicate);
		//					criteriaQuery.where(predicate);
		//					TypedQuery<Integer> query = getEntityManager().createQuery(criteriaQuery);
		//					return query.getResultList().size() > 0;
		//				}
		//			}
		//		}

	}

	@Override
	@SafeVarargs
	public final boolean isParentRecordExclude(ID rid, Class<? extends BaseEntity>... excludeEntities) {
		//		if (rid == null) {
		//			return false;
		//		}
		//		// get table name from entity
		//		String tableName = entityClass.getAnnotation(Table.class).name();
		//
		//		DatabaseMetaDataService databaseMetaDataService = (DatabaseMetaDataService) SpringUtil.getBean("databaseMetaDataService");
		//
		//		List<ParentChildTableVw> childTables = Collections.synchronizedList(databaseMetaDataService.getChildTables(tableName));
		//
		//		if (CollectionUtils.isListEmpty(childTables)) {
		//			System.err.println(
		//					"WARNING : " + tableName + " is NOT referenced by any table, maybe you have to check usage in different way !!");
		//		}
		//
		//		List<ParentChildTableVw> childTablesCopy = childTables.stream().collect(Collectors.toList());
		//
		//		// exclude
		//		for (int i = 0; i < excludeEntities.length; i++) {
		//			String excludeTableName = excludeEntities[i].getAnnotation(Table.class).name();
		//			childTablesCopy.removeAll(
		//					childTablesCopy.stream().filter(t -> t.getChildTableName().equals(excludeTableName)).collect(Collectors.toList()));
		//		}
		//
		//		for (ParentChildTableVw ct : childTablesCopy) {
		//			StringBuffer query = new StringBuffer();
		//			query	.append("SELECT 1 FROM ")
		//					.append(ct.getChildTableOwner())
		//					.append(".")
		//					.append(ct.getChildTableName())
		//					.append(" WHERE ")
		//					.append(ct.getRefColumnName())
		//					.append(" = ")
		//					.append(rid);
		//
		//			Query nativeQuery = getEntityManager().createNativeQuery(query.toString());
		//			if (nativeQuery.getResultList().size() > 0)
		//				return true;
		//
		//		}
		//		return false;
		//	}

		//	static boolean isInstanceOfAdmTrialApp(Object myVar) {
		//		return (myVar instanceof AdmTrialApplication);
		return false;
	}

	@Override
	public void delete(T entity) {
		super.delete(entity);
		super.flush();
	}

	@Override
	public boolean isRelationLoaded(BaseEntity entity, String attributeName) {
		if (entity == null)
			return true;

		PersistenceUnitUtil unitUtil = getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();
		return unitUtil.isLoaded(entity, attributeName);
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public boolean isEntityPersisted(BaseEntity entity) {
		if (entity == null || entity.getRid() == null)
			return false;

		return entityManager.contains(entity) || entityManager.find(entity.getClass(), entity.getRid()) != null;
	}

	@Override
	public Page<T> find(List<SearchCriterion> filters, Pageable pageable, Class<T> entityClass,
			String... join) {
		return findAll(getFilterSpecification(filters, entityClass, join), pageable);
	}

	@Override
	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, Sort sort,
			String... join) {
		return findAll(getFilterSpecification(filters, entityClass, join), sort);
	}

	@Override
	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, String... join) {
		return findAll(getFilterSpecification(filters, entityClass, join));
	}

	@Override
	public T findOne(List<SearchCriterion> filters, Class<T> entityClass, String... join) {
		return findOne(getFilterSpecification(filters, entityClass, join));
	}

	private Predicate alwaysTrue(CriteriaBuilder builder) {
		return builder.isTrue(builder.literal(true));
	}

	private boolean isFilterContainedInJoins(String joinKey, String... join) {
		for (String joinName : join) {
			if (joinName.startsWith(joinKey)) {
				return true;
			}
		}
		return false;
	}

	private <X> void addFetches(JoinWrapper<X> joinWrapper, String[] keys, int level) {
		JoinWrapper<X> childWrapper = joinWrapper.get(keys[level]);
		if (childWrapper == null) {
			childWrapper = new JoinWrapper<X>();
			joinWrapper.put(keys[level], childWrapper);
			childWrapper.setFetch(joinWrapper.getFetch().fetch(keys[level], JoinType.LEFT));
		}
		if (level < keys.length - 1) {
			addFetches(childWrapper, keys, level + 1);
		}
	}

	private Specification<T> getFilterSpecification(List<SearchCriterion> filterValues, Class<T> entityClass, String... join) {

		//TODO: ADD "ORDER BY CASE WHEN ... THEN ..."  https://stackoverflow.com/questions/46541922/jpa-criteriaquery-order-by-with-two-criteria 
		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) ->
			{
				if (query.getResultType().equals(getDomainClass())) {
					JoinWrapper<T> rootWrapper = new JoinWrapper<T>();
					for (String joinName : join) {
						String[] keys = joinName.split("[.]");

						JoinWrapper<T> joinWrapper = rootWrapper.get(keys[0]);
						if (joinWrapper == null) {
							joinWrapper = new JoinWrapper<T>();
							rootWrapper.put(keys[0], joinWrapper);
							joinWrapper.setFetch(root.fetch(keys[0], JoinType.LEFT));
						}

						if (keys.length > 1) {
							addFetches(joinWrapper, keys, 1);
						}
					}
				}

				Optional<Predicate> orPredicate = filterValues	.stream()
																.filter(entry -> predicateFilter(JunctionOperator.Or, entry))
																.map(entry -> getPredicate(root, builder, entry, entityClass, join))
																.reduce((a, b) -> builder.or(a, b));

				Optional<Predicate> andPredicate = filterValues	.stream()
																.filter(entry -> predicateFilter(JunctionOperator.And, entry))
																.map(entry -> getPredicate(root, builder, entry, entityClass, join))
																.reduce((a, b) -> builder.and(a, b));

				List<Predicate> predicates = new ArrayList<Predicate>();
				if (andPredicate.isPresent()) {
					predicates.add(andPredicate.get());
				}
				if (orPredicate.isPresent()) {
					predicates.add(orPredicate.get());
				}
				Optional<Predicate> predicate = predicates.stream().reduce((a, b) -> builder.and(a, b));
				return predicate.orElseGet(() -> alwaysTrue(builder));
			};
	}

	private Boolean predicateFilter(JunctionOperator junctionOperator, SearchCriterion entry) {
		if (entry.getJunctionOperator() != junctionOperator) {
			return false;
		}
		switch (entry.getOperator()) {
			default:
				break;
			case isnull:
			case isnotnull:
			case isnotempty:
			case isempty:
				return true;
		}
		return entry.getValue() != null;
	}

	private Predicate getPredicate(Root<T> root, CriteriaBuilder builder, SearchCriterion entry, Class<T> entityClass, String... join) {
		Object value = entry.getValue();
		Path<?> path = root;
		String key = entry.getField();
		if (key.contains(".")) {
			String joinKey = key.substring(0, key.lastIndexOf("."));
			key = key.substring(key.lastIndexOf(".") + 1);
			boolean contained = isFilterContainedInJoins(joinKey, join);
			//TODO these two fail when using a nested filter on a collection
			if (contained) {
				path = root.get(joinKey);
			} else {
				path = root.join(joinKey, JoinType.LEFT);
			}
		}

		Field valueField = ReflectionUtil.getAllFieldTypes(entityClass).get(key);
		if (valueField != null) {// because the key could be in another entity
			Class<?> fieldType = valueField.getType();
			if (fieldType.equals(Date.class) && !(value instanceof Date)) {
				try {
					ISO8601DateFormat jacksonDateFormat = new ISO8601DateFormat();
					value = jacksonDateFormat.parse((String) value);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		Expression<String> strKeyExpr;

		//TODO: SHOULD WE CAST EVERTHING TO TEXT? SINCE SOME STRINGS MAY HAVE SIZE BIGGER THAN 255
		// Cast to TEXT instead of varchar(255)[default] if the field is a TransField
		if (valueField != null && valueField.getType().equals(TransField.class)) {
			strKeyExpr = builder.function("lower", String.class, path.get(key).as(TextType.class));
		} else {
			strKeyExpr = builder.lower(path.get(key).as(String.class));
		}

		Predicate returnPredicate = null;

		switch (entry.getOperator()) {
			case contains:
				returnPredicate = builder.like(strKeyExpr, ("%" + value + "%").toLowerCase());
				break;
			case doesnotcontain:
				returnPredicate = builder.notLike(strKeyExpr, ("%" + value + "%").toLowerCase());
				break;
			case startswith:
				returnPredicate = builder.like(strKeyExpr, (value + "%").toLowerCase());
				break;
			case endswith:
				returnPredicate = builder.like(strKeyExpr, ("%" + value).toLowerCase());
				break;
			case isempty:
				returnPredicate = builder.equal(strKeyExpr, "");
				break;
			case isnotempty:
				returnPredicate = builder.notEqual(strKeyExpr, "");
				break;
			case eq:
				if (value instanceof String) {
					returnPredicate = builder.equal(strKeyExpr, value.toString().toLowerCase());
				} else {
					returnPredicate = builder.equal(path.get(key), value);
				}
				break;
			case neq:
				if (value instanceof String) {
					returnPredicate = builder.notEqual(strKeyExpr, value.toString().toLowerCase());
				} else {
					returnPredicate = builder.notEqual(path.get(key), value);
				}
				break;
			case gt:
				returnPredicate = builder.gt(path.get(key), (Number) value);
				break;
			case gte:
				if (value instanceof Date) {
					returnPredicate = builder.greaterThanOrEqualTo(path.get(key), (Date) value);
				} else {
					returnPredicate = builder.ge(path.get(key), (Number) value);
				}
				break;
			case lt:
				returnPredicate = builder.lt(path.get(key), (Number) value);
				break;
			case lte:
				if (value instanceof Date) {
					returnPredicate = builder.lessThanOrEqualTo(path.get(key), (Date) value);
				} else {
					returnPredicate = builder.le(path.get(key), (Number) value);
				}
				break;
			case isnotnull:
				returnPredicate = builder.isNotNull(path.get(key));
				break;
			case isnull:
				returnPredicate = builder.isNull(path.get(key));
				break;
			case in:
				//TODO check generic types instead of only long
				@SuppressWarnings("unchecked")
				List<Long> ids = (List<Long>) value;
				returnPredicate = path.get(key).in(ids);
				break;
			default:
				returnPredicate = null;
				break;
		}
		return returnPredicate;
	}

}
