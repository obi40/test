package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * TestRequestForm.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Mar/22/2018
 * 
 */
@Entity
@Table(name = "test_request_form")
@Audited
public class TestRequestForm extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@Column(name = "description")
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField description;

	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	//bi-directional many-to-one association to TestRequestFormTestDefinition
	@OneToMany(mappedBy = "testRequestForm", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "testRequestForm", allowSetters = true)
	private Set<TestRequestFormTestDefinition> testRequestFormTestDefinitions;

	@Transient
	@JsonDeserialize
	@JsonIgnoreProperties("testRequestForm")
	private List<TestRequestFormTestDefinition> testRequestFormTestList;

	public TestRequestForm() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TransField getDescription() {
		return this.description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

	public TransField getName() {
		return this.name;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public Set<TestRequestFormTestDefinition> getTestRequestFormTestDefinitions() {
		return this.testRequestFormTestDefinitions;
	}

	public void setTestRequestFormTestDefinitions(Set<TestRequestFormTestDefinition> testRequestFormTestDefinitions) {
		this.testRequestFormTestDefinitions = testRequestFormTestDefinitions;
	}

	public List<TestRequestFormTestDefinition> getTestRequestFormTestList() {
		return testRequestFormTestList;
	}

	public void setTestRequestFormTestList(List<TestRequestFormTestDefinition> testRequestFormTestList) {
		this.testRequestFormTestList = testRequestFormTestList;
	}

}