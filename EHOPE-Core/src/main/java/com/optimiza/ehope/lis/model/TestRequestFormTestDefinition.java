package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * TestRequestFormTestDefinition.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Mar/22/2018
 * 
 */
@Entity
@Table(name = "test_request_form_test_definition")
@Audited
public class TestRequestFormTestDefinition extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	//bi-directional many-to-one association to TestDefinition
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_definition_id")
	private TestDefinition testDefinition;

	//bi-directional many-to-one association to TestRequestForm
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_request_form_id")
	private TestRequestForm testRequestForm;

	public TestRequestFormTestDefinition() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TestDefinition getTestDefinition() {
		return this.testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public TestRequestForm getTestRequestForm() {
		return this.testRequestForm;
	}

	public void setTestRequestForm(TestRequestForm testRequestForm) {
		this.testRequestForm = testRequestForm;
	}

}