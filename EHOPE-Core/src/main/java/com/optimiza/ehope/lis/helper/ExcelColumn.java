package com.optimiza.ehope.lis.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.lkp.helper.FieldType;

public class ExcelColumn {

	private String title;
	private FieldType type;
	private String originalField;

	private Class<? extends BaseEntity> entityClass;
	private List<? extends BaseEntity> entityValues;
	private Map<Long, BaseEntity> entityRidValueMap;
	private String labelField;

	public ExcelColumn(String title, FieldType type, String originalField) {
		super();
		this.title = title;
		this.type = type;
		this.originalField = originalField;
	}

	public ExcelColumn(String title, FieldType type, String originalField, Class<? extends BaseEntity> entityClass,
			String labelField) {
		super();
		this.title = title;
		this.type = type;
		this.originalField = originalField;
		this.entityClass = entityClass;
		this.labelField = labelField;
		this.entityValues = ReflectionUtil.getRepository(this.entityClass.getSimpleName()).findAll();
		this.entityRidValueMap = new HashMap<>();
		for (BaseEntity be : this.entityValues) {
			this.entityRidValueMap.put(be.getRid(), be);
		}
	}

	public Map<Long, BaseEntity> getEntityRidValueMap() {
		return entityRidValueMap;
	}

	public void setEntityRidValueMap(Map<Long, BaseEntity> entityRidValueMap) {
		this.entityRidValueMap = entityRidValueMap;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public String getOriginalField() {
		return originalField;
	}

	public void setOriginalField(String originalField) {
		this.originalField = originalField;
	}

	public Class<? extends BaseEntity> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<? extends BaseEntity> entityClass) {
		this.entityClass = entityClass;
	}

	public String getLabelField() {
		return labelField;
	}

	public void setLabelField(String labelField) {
		this.labelField = labelField;
	}

	public List<? extends BaseEntity> getEntityValues() {
		return entityValues;
	}

	public void setEntityValues(List<? extends BaseEntity> entityValues) {
		this.entityValues = entityValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExcelColumn [title=");
		builder.append(title);
		builder.append(", type=");
		builder.append(type);
		builder.append(", originalField=");
		builder.append(originalField);
		builder.append(", entityClass=");
		builder.append(entityClass);
		builder.append(", labelField=");
		builder.append(labelField);
		builder.append("]");
		return builder.toString();
	}
}
