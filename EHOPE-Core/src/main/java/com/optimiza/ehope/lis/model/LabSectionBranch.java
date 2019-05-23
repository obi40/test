package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * The persistent class for the lab_section_branch database table.
 * 
 */
@Entity
@Table(name = "lab_section_branch")
@Audited
public class LabSectionBranch extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	//bi-directional many-to-one association to LabBranch
	@NotNull
	@ManyToOne
	@JoinColumn(name = "branch_id")
	private LabBranch labBranch;

	//bi-directional many-to-one association to LabSection
	@NotNull
	@ManyToOne
	@JoinColumn(name = "section_id")
	private LabSection labSection;

	public LabSectionBranch() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public LabBranch getLabBranch() {
		return this.labBranch;
	}

	public void setLabBranch(LabBranch labBranch) {
		this.labBranch = labBranch;
	}

	public LabSection getLabSection() {
		return this.labSection;
	}

	public void setLabSection(LabSection labSection) {
		this.labSection = labSection;
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
		LabSectionBranch other = (LabSectionBranch) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabSectionBranch [rid=" + rid + "]";
	}

}