package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Column;
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
 * TestForm.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Aug/3/2017
 * 
 */
@Entity
@Table(name = "test_form")
@Audited
public class TestForm extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	private String description;

	@Column(name = "standard_code")
	private String standardCode;

	private String method;

	@Column(name = "reporting_description")
	private String reportingDescription;

	//bi-directional many-to-one association to TestDefinition
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	private TestDefinition testDefinition;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "extra_test_id")
	private ExtraTest extraTest;

	public TestForm() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStandardCode() {
		return this.standardCode;
	}

	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getReportingDescription() {
		return this.reportingDescription;
	}

	public void setReportingDescription(String reportingDescription) {
		this.reportingDescription = reportingDescription;
	}

	public TestDefinition getTestDefinition() {
		return this.testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public ExtraTest getExtraTest() {
		return extraTest;
	}

	public void setExtraTest(ExtraTest extraTest) {
		this.extraTest = extraTest;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((standardCode == null) ? 0 : standardCode.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((reportingDescription == null) ? 0 : reportingDescription.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestForm other = (TestForm) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (standardCode == null) {
			if (other.standardCode != null)
				return false;
		} else if (!standardCode.equals(other.standardCode))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (reportingDescription == null) {
			if (other.reportingDescription != null)
				return false;
		} else if (!reportingDescription.equals(other.reportingDescription))
			return false;
		return true;
	}

}