package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Basic;
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

import com.optimiza.core.base.entity.BaseAuditableEntity;

/**
 * The persistent class for the emr_patient_indicators database table.
 * 
 */
@Entity
@Table(name = "emr_patient_indicators")
@Audited
public class EmrPatientIndicator extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	@Column(name = "expiry_date")
	private Date expiryDate;

	@Column(name = "patient_id")
	private EmrPatientInfo patient;

	@Column(name = "start_date")
	private Date startDate;

	//bi-directional many-to-one association to ComIndicator
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "indicator_id")
	private ComIndicator comIndicator;

	public EmrPatientIndicator() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public EmrPatientInfo getPatientId() {
		return this.patient;
	}

	public void setPatientId(EmrPatientInfo patient) {
		this.patient = patient;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public ComIndicator getComIndicator() {
		return this.comIndicator;
	}

	public void setComIndicator(ComIndicator comIndicator) {
		this.comIndicator = comIndicator;
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
		EmrPatientIndicator other = (EmrPatientIndicator) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmrPatientIndicator [rid=" + rid + "]";
	}

}