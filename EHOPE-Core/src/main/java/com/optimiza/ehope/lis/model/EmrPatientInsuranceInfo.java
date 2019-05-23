package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpDependencyType;

/**
 * EmrPatientInsuranceInfo.java
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since Aug/7/2017
 */
@Entity
@Table(name = "emr_patient_insurance_info")
@Audited
public class EmrPatientInsuranceInfo extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	@NotNull
	@Column(name = "card_number")
	@Size(min = 1, max = 255)
	private String cardNumber;

	@Column(name = "coverage_percentage")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	private BigDecimal coveragePercentage;

	@Size(max = 255)
	private String employer;

	@Column(name = "expiry_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date expiryDate;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@NotNull
	@Column(name = "is_vip")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isVip;

	@NotNull
	@Column(name = "is_default")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isDefault;

	@Column(name = "issue_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date issueDate;

	@JoinColumn(name = "patient_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private EmrPatientInfo patient;

	@Column(name = "policy_no")
	@Size(max = 255)
	private String policyNo;

	@Size(max = 255)
	@Column(name = "subscriber")
	@NotNull
	private String subscriber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id")
	@NotNull
	private InsProvider insProvider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dependency_type_id")
	@NotNull
	private LkpDependencyType lkpDependencyType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id")
	@NotNull
	private InsProviderPlan insProviderPlan;

	public EmrPatientInsuranceInfo() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getCardNumber() {
		return this.cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public BigDecimal getCoveragePercentage() {
		return coveragePercentage;
	}

	public void setCoveragePercentage(BigDecimal coveragePercentage) {
		this.coveragePercentage = coveragePercentage;
	}

	public String getEmployer() {
		return this.employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVip() {
		return this.isVip;
	}

	public void setIsVip(Boolean isVip) {
		this.isVip = isVip;
	}

	public Date getIssueDate() {
		return this.issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public EmrPatientInfo getPatient() {
		return this.patient;
	}

	public void setPatient(EmrPatientInfo patient) {
		this.patient = patient;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}

	public InsProvider getInsProvider() {
		return this.insProvider;
	}

	public void setInsProvider(InsProvider insProvider) {
		this.insProvider = insProvider;
	}

	public LkpDependencyType getLkpDependencyType() {
		return this.lkpDependencyType;
	}

	public void setLkpDependencyType(LkpDependencyType lkpDependencyType) {
		this.lkpDependencyType = lkpDependencyType;
	}

	public InsProviderPlan getInsProviderPlan() {
		return insProviderPlan;
	}

	public void setInsProviderPlan(InsProviderPlan insProviderPlan) {
		this.insProviderPlan = insProviderPlan;
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
		EmrPatientInsuranceInfo other = (EmrPatientInsuranceInfo) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmrPatientInsuranceInfo [rid=" + rid + "]";
	}
}