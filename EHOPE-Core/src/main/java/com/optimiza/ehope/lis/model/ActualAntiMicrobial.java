package com.optimiza.ehope.lis.model;

import java.io.Serializable;

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
import com.optimiza.ehope.lis.lkp.model.LkpOrganismSensitivity;

/**
 * ActualAntiMicrobial.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/10/2018
 * 
 */
@Entity
@Table(name = "actual_anti_microbial")
@Audited
public class ActualAntiMicrobial extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actual_result_id")
	private LabTestActualResult actualResult;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "anti_microbial_id")
	private AntiMicrobial antiMicrobial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organism_sensitivity_id")
	private LkpOrganismSensitivity organismSensitivity;

	public ActualAntiMicrobial() {
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

	public AntiMicrobial getAntiMicrobial() {
		return antiMicrobial;
	}

	public void setAntiMicrobial(AntiMicrobial antiMicrobial) {
		this.antiMicrobial = antiMicrobial;
	}

	public LkpOrganismSensitivity getOrganismSensitivity() {
		return organismSensitivity;
	}

	public void setOrganismSensitivity(LkpOrganismSensitivity organismSensitivity) {
		this.organismSensitivity = organismSensitivity;
	}

}