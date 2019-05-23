package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * LabUnit.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Oct/12/2017
 **/
@Entity
@Table(name = "lab_units")
@Audited
public class LabUnit extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "recommended_report_abbreviation")
	private String recommendedReportAbbreviation;

	@Column(name = "unit_of_measure")
	private String unitOfMeasure;

	public LabUnit() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public String getRecommendedReportAbbreviation() {
		return recommendedReportAbbreviation;
	}

	public void setRecommendedReportAbbreviation(String recommendedReportAbbreviation) {
		this.recommendedReportAbbreviation = recommendedReportAbbreviation;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
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
		LabUnit other = (LabUnit) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

}