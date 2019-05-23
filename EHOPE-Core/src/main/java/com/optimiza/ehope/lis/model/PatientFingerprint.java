package com.optimiza.ehope.lis.model;

import java.io.Serializable;

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
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * PatientFingerprint.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Mar/24/2019
 * 
 */
@Entity
@Table(name = "patient_fingerprint")
@Audited
public class PatientFingerprint extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotAudited
	@Column(name = "image")
	private byte[] image;

	//bi-directional many-to-one association to EmrPatientInfo
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	private EmrPatientInfo patient;

	@Transient
	private Long patientId;

	@Column(name = "template")
	@Size(max = 4000)
	private String template;

	@Transient
	private Double score;

	public PatientFingerprint() {
	}

	public PatientFingerprint(Long patientId, String template) {
		this.patientId = patientId;
		this.template = template;
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public byte[] getImage() {
		return this.image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public EmrPatientInfo getPatient() {
		return this.patient;
	}

	public void setPatient(EmrPatientInfo patient) {
		this.patient = patient;
	}

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

}