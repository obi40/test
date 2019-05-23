package com.optimiza.ehope.lis.model;

import java.io.Serializable;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * LabBranchSeparationFactor.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/07/2018
 **/
@Entity
@Table(name = "lab_branch_separation_factor")
@Audited
public class LabBranchSeparationFactor extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "separation_factor_id")
	private LabSeparationFactor labSeparationFactor;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	public LabBranchSeparationFactor() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LabSeparationFactor getLabSeparationFactor() {
		return labSeparationFactor;
	}

	public void setLabSeparationFactor(LabSeparationFactor labSeparationFactor) {
		this.labSeparationFactor = labSeparationFactor;
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
		LabBranchSeparationFactor other = (LabBranchSeparationFactor) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabBranchSeparationFactor [rid=" + rid + "]";
	}

}