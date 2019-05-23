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
 * ActualOrganism.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/10/2018
 * 
 */
@Entity
@Table(name = "actual_organism")
@Audited
public class ActualOrganism extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actual_result_id")
	private LabTestActualResult actualResult;

	@Column(name = "colony_count")
	private String colonyCount;

	@Column(name = "organism")
	private String organism;

	public ActualOrganism() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public LabTestActualResult getActualResult() {
		return actualResult;
	}

	public void setActualResult(LabTestActualResult actualResult) {
		this.actualResult = actualResult;
	}

	public String getColonyCount() {
		return colonyCount;
	}

	public void setColonyCount(String colonyCount) {
		this.colonyCount = colonyCount;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

}