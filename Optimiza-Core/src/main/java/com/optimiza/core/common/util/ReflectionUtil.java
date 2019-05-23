package com.optimiza.core.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.metamodel.internal.EntityTypeImpl;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.validator.constraints.Email;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Primitives;
import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.FieldMetaData;

@Component
public class ReflectionUtil {

	/**
	 * Get all persisted classes in the whole workspace
	 * 
	 * @param entityManager
	 * @return Set of classes
	 */
	public static Set<Class<?>> getAllPersistedClasses(EntityManager entityManager) {
		Metamodel metamodel = entityManager.getMetamodel();
		Set<EntityType<?>> ents = metamodel.getEntities();
		Set<Class<?>> persistedClasses = new HashSet<>();
		for (EntityType<?> entityType : ents) {
			EntityTypeImpl<?> i = (EntityTypeImpl<?>) entityType;
			if (i.getJavaType() != null) {
				persistedClasses.add(i.getJavaType());
			}
		}
		return persistedClasses;
	}

	@SuppressWarnings("unchecked")
	public static Class<BaseEntity> getEntityClassByName(String entityName, EntityManager entityManager) {
		if (StringUtil.isEmpty(entityName)) {
			return null;
		}
		Metamodel metamodel = entityManager.getMetamodel();
		Set<EntityType<?>> ents = metamodel.getEntities();
		for (EntityType<?> entityType : ents) {
			EntityTypeImpl<?> i = (EntityTypeImpl<?>) entityType;
			if (i.getJavaType() != null) {
				if (entityName.equals(i.getJavaType().getSimpleName())) {
					return (Class<BaseEntity>) i.getJavaType();
				}
			}
		}
		return null;
	}

	public static String getFieldNameFromColumn(String entityName, String columnName, EntityManager entityManager) {
		Class<?> entityClass = getEntityClassByName(entityName, entityManager);
		Field[] fields = entityClass.getDeclaredFields();
		Annotation[] annotations;
		for (Field field : fields) {
			annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Column) {
					Column myAnnotation = (Column) annotation;
					if (myAnnotation.name().equals(columnName)) {
						return field.getName();
					}
				} else if (annotation instanceof JoinColumn) {
					JoinColumn myAnnotation = (JoinColumn) annotation;
					if (myAnnotation.name().equals(columnName)) {
						return field.getName();
					}
				}
			}
		}
		return columnName.toLowerCase();
	}

	/**
	 * Get the field type from a given entity using reflection
	 *
	 * @param entityName
	 * @param fieldName
	 * @return
	 */
	public static Class<?> getFieldType(String entityName, String fieldName, EntityManager entityManager) {
		Class<?> entityClass = getEntityClassByName(entityName, entityManager);
		Field field = null;
		try {
			field = entityClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return field.getType();
	}

	/**
	 * Search in joinEntity for a field of type mainEntity and return field name
	 *
	 * @param mainEntity
	 * @param joinEntity
	 * @param entityManager
	 * @return String
	 */
	public static String getJoinFieldName(String mainEntity, String joinEntity, EntityManager entityManager) {
		Class<?> clazz = getEntityClassByName(joinEntity, entityManager);
		Field[] fields = clazz.getDeclaredFields();
		String className;
		for (Field field : fields) {
			className = field.getType().getSimpleName();
			if (className.equals("List")) { // get the generic type
				ParameterizedType pType = ((ParameterizedType) field.getGenericType());
				Class<?> genClass = (Class<?>) pType.getActualTypeArguments()[0];
				className = genClass.getSimpleName();
			}
			if (className.equals(mainEntity)) {
				return field.getName();
			}
		}
		return "";
	}

	public static Class<?> getFieldDataType(Class<?> entityClass, String dbColumnName) {
		if (entityClass != null) {
			Class<?> tempClass = entityClass;
			try {
				Field field = tempClass.getDeclaredField(StringUtil.toLowerCamelCase(dbColumnName));
				Class<?> type = field.getType();
				if (type.isPrimitive()) {
					type = Primitives.wrap(type);
				}
				return type;
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				// String clsName = tempClass.getPackage().getName() + ".extended." + tempClass.getSimpleName() + "EX";
				// try {
				// tempClass = Class.forName(clsName);
				// return getFieldDataType(tempClass, dbColumnName);
				// } catch (ClassNotFoundException ex) {
				// ex.printStackTrace();
				// }
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Class<?> getFieldType(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName).getType();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get all the fields of the {@code clazz} even in the super classes
	 * 
	 * @param clazz
	 * @return Map which contains field name and Field object
	 */
	public static Map<String, Field> getAllFieldTypes(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		do {
			Collections.addAll(fields, clazz.getDeclaredFields());
			clazz = clazz.getSuperclass();
		} while (clazz != null);

		return fields	.stream().filter(item -> !item.getName().equals("serialVersionUID"))// remove serialVersionUID since it is duplicated in super classes
						.collect(Collectors.toMap(Field::getName, item -> item));
	}

	public static Map<String, FieldMetaData> getEntityFieldMetaData(Class<?> clazz) {

		Map<String, FieldMetaData> fieldMetaDataList = new HashMap<String, FieldMetaData>();

		FieldMetaData fieldMetaData = null;
		for (Field field : clazz.getDeclaredFields()) {

			fieldMetaData = new FieldMetaData();
			fieldMetaData.setName(field.getName());
			fieldMetaData.setNotNull(field.isAnnotationPresent(NotNull.class));
			fieldMetaData.setEmail(field.isAnnotationPresent(Email.class));
			//check if max is not present (0)
			if (field.isAnnotationPresent(Size.class) || field.isAnnotationPresent(Max.class) || field.isAnnotationPresent(Min.class)) {
				fieldMetaData.setSized(true);
			}
			if (field.isAnnotationPresent(Size.class)) {
				Size size = field.getAnnotation(Size.class);
				fieldMetaData.setMin(size.min());
				fieldMetaData.setMax(size.max());

			} else {
				if (field.isAnnotationPresent(Max.class)) {
					Max max = field.getAnnotation(Max.class);
					fieldMetaData.setMax((int) max.value());
				}
				if (field.isAnnotationPresent(Min.class)) {
					Min min = field.getAnnotation(Min.class);
					fieldMetaData.setMin((int) min.value());
				}

			}

			if (field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);
				fieldMetaData.setUpdatable(column.updatable());
				fieldMetaData.setColumnName(column.name());
			}

			if (field.isAnnotationPresent(Digits.class)) {
				Digits digits = field.getAnnotation(Digits.class);
				fieldMetaData.setInteger(digits.integer());
				fieldMetaData.setFraction(digits.fraction());
			}

			fieldMetaDataList.put(field.getName(), fieldMetaData);
		}

		return fieldMetaDataList;

	}

	/**
	 * Convert from an unknown object type(written as string) to an object.
	 * 
	 * @param className
	 * @param stringObj : the object to deserialize (must be in string format)
	 * @param entityManager
	 * @return The serialized object
	 */
	public static BaseEntity getObjectFromUknownType(String className, String stringObj, EntityManager entityManager) {
		Class<? extends BaseEntity> clazz = getEntityClassByName(className, entityManager);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(stringObj, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Check all constraints on the entity and test it on objToCheck.
	 * 
	 * @param objToCheck
	 * @return True if the passed object violates any constraints, otherwise false
	 */
	public static boolean doesEntityViolateConstraints(Object objToCheck) {

		Map<String, FieldMetaData> meta = getEntityFieldMetaData(objToCheck.getClass());
		Field field = null;

		try {

			for (FieldMetaData fmd : meta.values()) {

				if (fmd.isEmail() || fmd.isUpdatable()) {
					continue;
				}

				field = objToCheck.getClass().getDeclaredField(fmd.getName());
				field.setAccessible(true);

				if (fmd.isNotNull() && Objects.isNull(field.get(objToCheck))) {
					System.out.println("Field[" + fmd.getName() + "] Is NULL With @NotNull");
					return true;
				}
				if (fmd.isSized()) {

					String valueToCheck = null;

					if (field.get(objToCheck) instanceof TransField) {

						TransField tf = (TransField) field.get(objToCheck);
						StringBuilder combinedKeyValue = new StringBuilder();
						for (Map.Entry<String, String> entry : tf.entrySet()) {
							combinedKeyValue.append("\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"");
							combinedKeyValue.append(",");
						}
						combinedKeyValue.replace(combinedKeyValue.length() - 1, combinedKeyValue.length(), "");
						combinedKeyValue.insert(0, "{");
						combinedKeyValue.append("}");
						valueToCheck = combinedKeyValue.toString();

					} else if (field.get(objToCheck) instanceof String) {
						valueToCheck = (String) field.get(objToCheck);
					} else if (field.get(objToCheck) instanceof Number) {
						valueToCheck = ((Number) field.get(objToCheck)).toString();
					}

					// the type is not any of the above or the field is null
					if (Objects.isNull(valueToCheck) || Objects.isNull(field.get(objToCheck))) {
						System.out.println("Can't Decide With: A Null Field[" + fmd.getName() + "]");
						return true;
					}

					if (fmd.getMin() == 0 && valueToCheck.length() > fmd.getMax()) {
						System.out.println("Field[" + fmd.getName() + "] Length Is Bigger Than The Max[" + fmd.getMax() + "]");
						return true;

					} else if (fmd.getMax() == Integer.MAX_VALUE && valueToCheck.length() < fmd.getMin()) {
						System.out.println("Field[" + fmd.getName() + "] Length Is Smaller Than The Min[" + fmd.getMin() + "]");
						return true;

					} else if (valueToCheck.length() < fmd.getMin() || valueToCheck.length() > fmd.getMax()) {
						System.out.println("Field[" + fmd.getName() + "] Length Is Violating Min[" + fmd.getMin() + "] OR The Max["
								+ fmd.getMax() + "]");
						return true;

					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Unproxy the Hibernate object and the entities inside it.
	 * Warning: un-proxying a non-fetched field will fire a query to fetch it.
	 * 
	 * @param proxiedObject : object to unproxy.
	 * @param fieldsToUnproxy : fields to unproxy,the names should be the same in the proxiedObject
	 * @return Unproxied object with selected-unproxied entities inside of it
	 */
	public static <T> T deepUnproxy(T proxiedObject, List<String> fieldsToUnproxy) {
		proxiedObject = unproxy(proxiedObject);
		Field[] fields = proxiedObject.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				if (fieldsToUnproxy.contains(field.getName()) && field.get(proxiedObject) != null) {
					field.set(proxiedObject, unproxy(field.get(proxiedObject)));
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return proxiedObject;
	}

	/**
	 * Unproxy the Hibernate lazy object
	 * 
	 * @param proxy
	 * @return unproxied object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unproxy(T proxy) {
		if (proxy instanceof HibernateProxy) {
			HibernateProxy hibernateProxy = (HibernateProxy) proxy;
			LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
			return (T) initializer.getImplementation();
		} else {
			return proxy;
		}
	}

	/**
	 * Get repository of the entityName by reflection
	 * 
	 * @param entityName
	 * @return entity's repository
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BaseEntity> GenericRepository<T> getRepository(String entityName) {
		Object obj = SpringUtil.getApplicationContext().getBean(entityName + "Repo");
		GenericRepository<T> repo = (GenericRepository<T>) obj;
		return repo;
	}

	/**
	 * Save any entity to the database, must auto wire the ReflectionUtil.
	 * Must AutoWire the ReflectionUtil.
	 * 
	 * @param entity
	 * @return saved entity
	 */
	public <T extends BaseEntity> T saveEntity(T entity) {
		return getRepository(entity.getClass().getSimpleName()).save(entity);
	}

	/**
	 * Save any entity to the database, must auto wire the ReflectionUtil.
	 * Must AutoWire the ReflectionUtil.
	 * Use this if the saveEntity is being called in a loop.
	 * 
	 * @param repo
	 * @param entity
	 * @return saved entity
	 */
	public <T extends BaseEntity> T saveEntity(GenericRepository<T> repo, T entity) {
		return repo.save(entity);
	}

	/**
	 * Save any entity to the database in a new transaction.
	 * Must AutoWire the ReflectionUtil.
	 * 
	 * @param entity
	 * @return saved entity
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T extends BaseEntity> T saveEntitySeparate(T entity) {
		return saveEntity(entity);
	}

	/**
	 * Save any entity to the database in a new transaction.
	 * Must AutoWire the ReflectionUtil.
	 * Use this if the saveEntitySeparate is being called in a loop.
	 * 
	 * @param repo
	 * @param entity
	 * @return saved entity
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T extends BaseEntity> T saveEntitySeparate(GenericRepository<T> repo, T entity) {
		return saveEntity(repo, entity);
	}

	public static <T> Boolean doesEntityHaveField(Class<T> clazz, String fieldName) {
		Field[] properties = clazz.getDeclaredFields();
		for (Field field : properties) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Disable the Intercepter in case of using a function in a parent function that enabled filters
	 * 
	 * @param entityManager
	 * @param disableBranchFilter
	 * @param disableTenantFilter
	 * @param supplier : the function to disable filters
	 * @return supplier data
	 */
	public static <T> T disableIntercepterFilters(EntityManager entityManager, boolean disableBranchFilter, boolean disableTenantFilter,
			Supplier<T> supplier) {
		//TODO: add Consumer overloaded
		Session session = entityManager.unwrap(Session.class);
		Filter tenantFilter = null;
		Filter branchFilter = null;
		if (disableTenantFilter) {
			tenantFilter = session.getEnabledFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
			if (tenantFilter != null) {
				session.disableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
			}
		}
		if (disableBranchFilter) {
			branchFilter = session.getEnabledFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			if (branchFilter != null) {
				session.disableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			}
		}

		T result = supplier.get();
		if (tenantFilter != null) {
			tenantFilter = session.enableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
			tenantFilter.setParameter("tenantId", SecurityUtil.getCurrentUser().getTenantId());
			tenantFilter.validate();

		}
		if (branchFilter != null) {
			branchFilter = session.enableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			branchFilter.setParameter("branchId", SecurityUtil.getCurrentUser().getBranchId());
			branchFilter.validate();
		}
		return result;
	}

}
