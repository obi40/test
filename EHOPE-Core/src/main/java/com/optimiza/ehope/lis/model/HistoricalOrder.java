package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseHistoricalEntity;

/**
 * HistoricalResult.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/11/2018
 * 
 */
@Entity
@Table(name = "historical_order")
public class HistoricalOrder extends BaseHistoricalEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@NotNull
	@Column(name = "order_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date orderDate;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "order_number")
	private String orderNumber;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "patient_file_no")
	private String patientFileNo;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	private EmrPatientInfo patient;

	//bi-directional many-to-one association to HistoricalTest
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "order" }, allowSetters = true)
	private Set<HistoricalTest> tests;

	public HistoricalOrder() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getOrderDate() {
		return this.orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderNumber() {
		return this.orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPatientFileNo() {
		return this.patientFileNo;
	}

	public void setPatientFileNo(String patientFileNo) {
		this.patientFileNo = patientFileNo;
	}

	public EmrPatientInfo getPatient() {
		return patient;
	}

	public void setPatient(EmrPatientInfo patient) {
		this.patient = patient;
	}

	public Set<HistoricalTest> getTests() {
		return tests;
	}

	public void setTests(Set<HistoricalTest> tests) {
		this.tests = tests;
	}

}