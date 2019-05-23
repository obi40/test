package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.List;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpSectionType;

/**
 * LabSection.java
 * 
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since jun/19/2017
 */
@Entity
@Table(name = "lab_section")
@Audited
public class LabSection extends BaseAuditableTenantedEntity implements Serializable, Comparable<LabSection> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	@NotNull
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id")
	private LkpSectionType type;

	@NotNull
	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "is_active")
	private Boolean isActive;

	@OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "section" }, allowSetters = true)
	private Set<BillClassification> classification;

	//bi-directional many-to-one association to LabSectionBranch
	@OneToMany(mappedBy = "labSection", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("labSection")
	private List<LabSectionBranch> labSectionBranches;

	@OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("section")
	private List<TestDefinition> testDefinitionList;

	@NotNull
	@Column(name = "rank")
	private Long rank;

	@Transient
	@JsonProperty
	private Long oldRank;

	public LabSection() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TransField getName() {
		return this.name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public LkpSectionType getType() {
		return type;
	}

	public void setType(LkpSectionType type) {
		this.type = type;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Set<BillClassification> getClassification() {
		return classification;
	}

	public void setClassification(Set<BillClassification> classification) {
		this.classification = classification;
	}

	public List<LabSectionBranch> getLabSectionBranches() {
		return this.labSectionBranches;
	}

	public void setLabSectionBranches(List<LabSectionBranch> labSectionBranches) {
		this.labSectionBranches = labSectionBranches;
	}

	public List<TestDefinition> getTestDefinitionList() {
		return testDefinitionList;
	}

	public void setTestDefinitionList(List<TestDefinition> testDefinitionList) {
		this.testDefinitionList = testDefinitionList;
	}

	public LabSectionBranch addLabSectionBranch(LabSectionBranch labSectionBranch) {
		getLabSectionBranches().add(labSectionBranch);
		labSectionBranch.setLabSection(this);

		return labSectionBranch;
	}

	public LabSectionBranch removeLabSectionBranch(LabSectionBranch labSectionBranch) {
		getLabSectionBranches().remove(labSectionBranch);
		labSectionBranch.setLabSection(null);

		return labSectionBranch;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public Long getOldRank() {
		return oldRank;
	}

	public void setOldRank(Long oldRank) {
		this.oldRank = oldRank;
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
		LabSection other = (LabSection) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabSection [rid=" + rid + "]";
	}

	@Override
	public int compareTo(LabSection other) {
		return rank.compareTo(other.getRank());
	}

}