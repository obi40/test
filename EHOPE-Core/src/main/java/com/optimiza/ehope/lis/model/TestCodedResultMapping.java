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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * TestCodedResultMapping.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jul/11/2018
 * 
 */
@Entity
@Table(name = "test_coded_result_mapping")
@Audited
public class TestCodedResultMapping extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	//bi-directional many-to-one association to TestResult
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "result_id")
	@JsonIgnoreProperties({ "testCodedResultMappings" })
	private TestResult testResult;

	//bi-directional many-to-one association to TestCodedResult
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "code_id")
	@JsonIgnoreProperties({ "testCodedResultMappings" })
	private TestCodedResult testCodedResult;

	public TestCodedResultMapping() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	public TestCodedResult getTestCodedResult() {
		return this.testCodedResult;
	}

	public void setTestCodedResult(TestCodedResult testCodedResult) {
		this.testCodedResult = testCodedResult;
	}

}