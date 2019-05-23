package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpTestEntryType;

/**
 * ExtraTest.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Aug/21/2017
 * 
 */
@Entity
@Table(name = "extra_test")
@Audited
public class ExtraTest extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@Column(name = "always_performed")
	@NotNull
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean alwaysPerformed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entry_type_id")
	private LkpTestEntryType entryType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "extra_test_id")
	@JsonIgnoreProperties(value = { "extraTests", "tests", "extraTestList", "section" }, allowSetters = true)
	private TestDefinition extraTest;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	@JsonIgnoreProperties(value = { "extraTests", "tests", "extraTestList", "section" }, allowSetters = true)
	private TestDefinition test;

	public ExtraTest() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Boolean getAlwaysPerformed() {
		return this.alwaysPerformed;
	}

	public void setAlwaysPerformed(Boolean alwaysPerformed) {
		this.alwaysPerformed = alwaysPerformed;
	}

	public LkpTestEntryType getEntryType() {
		return entryType;
	}

	public void setEntryType(LkpTestEntryType entryType) {
		this.entryType = entryType;
	}

	public TestDefinition getExtraTest() {
		return extraTest;
	}

	public void setExtraTest(TestDefinition extraTest) {
		this.extraTest = extraTest;
	}

	public TestDefinition getTest() {
		return test;
	}

	public void setTest(TestDefinition test) {
		this.test = test;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alwaysPerformed == null) ? 0 : alwaysPerformed.hashCode());
		result = prime * result + ((entryType == null) ? 0 : entryType.hashCode());
		result = prime * result + ((extraTest == null) ? 0 : extraTest.hashCode());
		result = prime * result + ((rid == null) ? 0 : rid.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtraTest other = (ExtraTest) obj;
		if (alwaysPerformed == null) {
			if (other.alwaysPerformed != null)
				return false;
		} else if (!alwaysPerformed.equals(other.alwaysPerformed))
			return false;
		if (entryType == null) {
			if (other.entryType != null)
				return false;
		} else if (!entryType.equals(other.entryType))
			return false;
		if (extraTest == null) {
			if (other.extraTest != null)
				return false;
		} else if (!extraTest.equals(other.extraTest))
			return false;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		if (test == null) {
			if (other.test != null)
				return false;
		} else if (!test.equals(other.test))
			return false;
		return true;
	}

}