package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * TestCodedResult.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jul/11/2018
 * 
 */
@Entity
@Table(name = "test_coded_result")
@Audited
public class TestCodedResult extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@NotNull
	@Column(unique = true)
	private String code;

	@NotNull
	private String value;

	//bi-directional many-to-one association to TestCodedResultMapping
	@OneToMany(mappedBy = "testCodedResult", fetch = FetchType.LAZY)
	private Set<TestCodedResultMapping> testCodedResultMappings;

	public TestCodedResult() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Set<TestCodedResultMapping> getTestCodedResultMappings() {
		return testCodedResultMappings;
	}

	public void setTestCodedResultMappings(Set<TestCodedResultMapping> testCodedResultMappings) {
		this.testCodedResultMappings = testCodedResultMappings;
	}

}