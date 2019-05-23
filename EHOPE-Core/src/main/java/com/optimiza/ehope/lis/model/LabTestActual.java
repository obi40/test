package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;

/**
 * The persistent class for the lab_test_actual database table.
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since 14/6/2017 --- dd/mm/yyyy
 */
@Entity
@Table(name = "lab_test_actual")
@Audited
public class LabTestActual extends BaseAuditableBranchedEntity implements Serializable, Comparable<LabTestActual> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_definition_id")
	private TestDefinition testDefinition;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sample_id")
	private LabSample labSample;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operation_status_id")
	private LkpOperationStatus lkpOperationStatus;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_destination_id")
	private TestDestination testDestination;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_actual_test_id")
	@JsonIgnoreProperties(value = { "sourceActualTest", "reorderedActualTests", "labSample" }, allowSetters = true)
	private LabTestActual sourceActualTest;

	@OneToMany(mappedBy = "sourceActualTest", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "sourceActualTest", "reorderedActualTests" }, allowSetters = true)
	private Set<LabTestActual> reorderedActualTests;

	@OneToMany(mappedBy = "actualTest", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "actualTest" }, allowSetters = true)
	private Set<ActualTestArtifact> artifacts;

	@OneToMany(mappedBy = "labTestActual", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "labTestActual" }, allowSetters = true)
	private Set<LabTestActualResult> labTestActualResults;

	@OneToMany(mappedBy = "labTestActual", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "labTestActual" }, allowSetters = true)
	private Set<LabTestAnswer> labTestAnswerSet;

	//bi-directional one-to-one association to BillPatientTransaction
	@OneToMany(mappedBy = "labTestActual", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "labTestActual" }, allowSetters = true)
	private Set<BillChargeSlip> billChargeSlipList;

	@Transient
	@JsonSerialize
	private List<LabTestActualResult> labTestActualResultList;

	@Transient
	@JsonSerialize
	private Boolean isPrintPrevious;

	public Boolean getIsPrintPrevious() {
		return isPrintPrevious;
	}

	public void setIsPrintPrevious(Boolean isPrintPrevious) {
		this.isPrintPrevious = isPrintPrevious;
	}

	public LabTestActual() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TestDestination getTestDestination() {
		return testDestination;
	}

	public void setTestDestination(TestDestination testDestination) {
		this.testDestination = testDestination;
	}

	public LabTestActual getSourceActualTest() {
		return sourceActualTest;
	}

	public void setSourceActualTest(LabTestActual sourceActualTest) {
		this.sourceActualTest = sourceActualTest;
	}

	public Set<LabTestActual> getReorderedActualTests() {
		return reorderedActualTests;
	}

	public void setReorderedActualTests(Set<LabTestActual> reorderedActualTests) {
		this.reorderedActualTests = reorderedActualTests;
	}

	public Set<ActualTestArtifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(Set<ActualTestArtifact> artifacts) {
		this.artifacts = artifacts;
	}

	public Set<BillChargeSlip> getBillChargeSlipList() {
		return billChargeSlipList;
	}

	public void setBillChargeSlipList(Set<BillChargeSlip> billChargeSlipList) {
		this.billChargeSlipList = billChargeSlipList;
	}

	public LkpOperationStatus getLkpOperationStatus() {
		return lkpOperationStatus;
	}

	public void setLkpOperationStatus(LkpOperationStatus lkpOperationStatus) {
		this.lkpOperationStatus = lkpOperationStatus;
	}

	public TestDefinition getTestDefinition() {
		return testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public LabSample getLabSample() {
		return labSample;
	}

	public void setLabSample(LabSample labSample) {
		this.labSample = labSample;
	}

	public Set<LabTestActualResult> getLabTestActualResults() {
		return labTestActualResults;
	}

	public void setLabTestActualResults(Set<LabTestActualResult> labTestActualResults) {
		this.labTestActualResults = labTestActualResults;
	}

	public Set<LabTestAnswer> getLabTestAnswerSet() {
		return labTestAnswerSet;
	}

	public void setLabTestAnswerSet(Set<LabTestAnswer> labTestAnswerSet) {
		this.labTestAnswerSet = labTestAnswerSet;
	}

	public List<LabTestActualResult> getLabTestActualResultList() {
		return labTestActualResultList;
	}

	public void setLabTestActualResultList(List<LabTestActualResult> labTestActualResultList) {
		this.labTestActualResultList = labTestActualResultList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rid == null) ? 0 : rid.hashCode());
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
		LabTestActual other = (LabTestActual) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabTestActual [rid=" + rid + "]";
	}

	@Override
	public int compareTo(LabTestActual other) {
		return testDefinition.compareTo(other.getTestDefinition());
	}

}