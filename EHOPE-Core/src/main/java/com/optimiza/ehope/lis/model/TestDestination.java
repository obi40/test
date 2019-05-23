package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpTestDestinationType;

/**
 * TestDestination.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Sep/18/2018
 * 
 */
@Entity
@Table(name = "test_destination")
@Audited
public class TestDestination extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	//bi-directional many-to-one association to InsProvider
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ins_provider_id")
	private InsProvider destinationBranch;

	//bi-directional many-to-one association to InsProvider
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_id")
	@NotNull
	private InsProvider source;

	//bi-directional many-to-one association to TestDefinition
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	@NotNull
	private TestDefinition testDefinition;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	//bi-directional many-to-one association to Workbench
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workbench_id")
	private Workbench workbench;

	//bi-directional many-to-one association to LkpTestDestinationType
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id")
	@NotNull
	private LkpTestDestinationType type;

	//bi-directional many-to-one association to TestNormalRange
	@OneToMany(mappedBy = "testDestination", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "testDestination" }, allowSetters = true)
	private Set<TestNormalRange> normalRanges;

	public TestDestination() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public InsProvider getDestinationBranch() {
		return destinationBranch;
	}

	public void setDestinationBranch(InsProvider destinationBranch) {
		this.destinationBranch = destinationBranch;
	}

	public InsProvider getSource() {
		return source;
	}

	public void setSource(InsProvider source) {
		this.source = source;
	}

	public TestDefinition getTestDefinition() {
		return testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Workbench getWorkbench() {
		return workbench;
	}

	public void setWorkbench(Workbench workbench) {
		this.workbench = workbench;
	}

	public LkpTestDestinationType getType() {
		return type;
	}

	public void setType(LkpTestDestinationType type) {
		this.type = type;
	}

	public Set<TestNormalRange> getNormalRanges() {
		return normalRanges;
	}

	public void setNormalRanges(Set<TestNormalRange> normalRanges) {
		this.normalRanges = normalRanges;
	}

}