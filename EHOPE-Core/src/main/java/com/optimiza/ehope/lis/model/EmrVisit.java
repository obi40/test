package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.lkp.model.LkpVisitType;

/**
 * The persistent class for the "emr_visits" database table.
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since Jun/13/2017
 */
@Entity
@Table(name = "emr_visits")
@Audited
public class EmrVisit extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "approval_number")
	@Size(min = 1, max = 255)
	private String approvalNumber;

	@NotNull
	@Column(name = "admission_number")
	@Size(min = 1, max = 255)
	private String admissionNumber;

	@Column(name = "ins_form_number")
	@Size(min = 1, max = 255)
	private String insFormNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_plan_id")
	private InsProviderPlan providerPlan;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pat_id")
	private EmrPatientInfo emrPatientInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merged_from_pat_id")
	private EmrPatientInfo mergedFromEmrPatientInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctor_id")
	private Doctor doctor;

	@NotNull
	@Column(name = "invoice_number")
	@Size(max = 255)
	private String invoiceNumber;

	@NotNull
	@Column(name = "visit_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date visitDate;

	@NotNull
	@Column(name = "is_stat")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isStat;

	@NotNull
	@Column(name = "is_sms_notification")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isSmsNotification;

	@NotNull
	@Column(name = "is_whatsapp_notification")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isWhatsappNotification;

	@NotNull
	@Column(name = "is_email_notification")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isEmailNotification;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "visit_type_id")
	private LkpVisitType visitType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operation_status_id")
	private LkpOperationStatus lkpOperationStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_insurance_info_id")
	private EmrPatientInsuranceInfo patientInsuranceInfo;

	//bi-directional many-to-one association to LabSample
	@OneToMany(mappedBy = "emrVisit", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "emrVisit" }, allowSetters = true)
	private Set<LabSample> labSamples;

	@Digits(integer = 15, fraction = 3)
	@Column(name = "paid_amount")
	private BigDecimal paidAmount;

	@Digits(integer = 15, fraction = 3)
	@Column(name = "total_amount")
	private BigDecimal totalAmount;

	@Column(name = "appointment_card_date")
	private Date appointmentCardDate;

	@Column(name = "appointment_card_notes")
	@Size(max = 255)
	private String appointmentCardNotes;

	@Column(name = "appointment_card_time")
	@Size(max = 255)
	private String appointmentCardTime;

	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "order" }, allowSetters = true)
	private Set<OrderArtifact> artifacts;

	@OneToMany(mappedBy = "emrVisit", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "emrVisit" }, allowSetters = true)
	private Set<EmrVisitGroup> visitGroups;

	@OneToMany(mappedBy = "visit", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "visit" }, allowSetters = true)
	private Set<BillBalanceTransaction> balanceTransactions;

	@Transient
	@JsonProperty
	private LabBranch branch;

	public EmrVisit() {
	}

	public EmrPatientInfo getMergedFromEmrPatientInfo() {
		return mergedFromEmrPatientInfo;
	}

	public void setMergedFromEmrPatientInfo(EmrPatientInfo mergedFromEmrPatientInfo) {
		this.mergedFromEmrPatientInfo = mergedFromEmrPatientInfo;
	}

	public Set<BillBalanceTransaction> getBalanceTransactions() {
		return balanceTransactions;
	}

	public void setBalanceTransactions(Set<BillBalanceTransaction> balanceTransactions) {
		this.balanceTransactions = balanceTransactions;
	}

	public String getInsFormNumber() {
		return insFormNumber;
	}

	public void setInsFormNumber(String insFormNumber) {
		this.insFormNumber = insFormNumber;
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Set<EmrVisitGroup> getVisitGroups() {
		return visitGroups;
	}

	public void setVisitGroups(Set<EmrVisitGroup> visitGroups) {
		this.visitGroups = visitGroups;
	}

	public InsProviderPlan getProviderPlan() {
		return providerPlan;
	}

	public void setProviderPlan(InsProviderPlan providerPlan) {
		this.providerPlan = providerPlan;
	}

	public LabBranch getBranch() {
		return branch;
	}

	public void setBranch(LabBranch branch) {
		this.branch = branch;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public EmrPatientInsuranceInfo getPatientInsuranceInfo() {
		return patientInsuranceInfo;
	}

	public void setPatientInsuranceInfo(EmrPatientInsuranceInfo patientInsuranceInfo) {
		this.patientInsuranceInfo = patientInsuranceInfo;
	}

	public LkpOperationStatus getLkpOperationStatus() {
		return lkpOperationStatus;
	}

	public void setLkpOperationStatus(LkpOperationStatus lkpOperationStatus) {
		this.lkpOperationStatus = lkpOperationStatus;
	}

	public String getAdmissionNumber() {
		return this.admissionNumber;
	}

	public void setAdmissionNumber(String admissionNumber) {
		this.admissionNumber = admissionNumber;
	}

	public EmrPatientInfo getEmrPatientInfo() {
		return this.emrPatientInfo;
	}

	public void setEmrPatientInfo(EmrPatientInfo emrPatientInfo) {
		this.emrPatientInfo = emrPatientInfo;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public Boolean getIsStat() {
		return isStat;
	}

	public void setIsStat(Boolean isStat) {
		this.isStat = isStat;
	}

	public String getApprovalNumber() {
		return approvalNumber;
	}

	public void setApprovalNumber(String approvalNumber) {
		this.approvalNumber = approvalNumber;
	}

	public Date getVisitDate() {
		return this.visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public Boolean getIsSmsNotification() {
		return isSmsNotification;
	}

	public void setIsSmsNotification(Boolean isSmsNotification) {
		this.isSmsNotification = isSmsNotification;
	}

	public Boolean getIsWhatsappNotification() {
		return isWhatsappNotification;
	}

	public void setIsWhatsappNotification(Boolean isWhatsappNotification) {
		this.isWhatsappNotification = isWhatsappNotification;
	}

	public Boolean getIsEmailNotification() {
		return isEmailNotification;
	}

	public void setIsEmailNotification(Boolean isEmailNotification) {
		this.isEmailNotification = isEmailNotification;
	}

	public LkpVisitType getVisitType() {
		return this.visitType;
	}

	public void setVisitType(LkpVisitType visitType) {
		this.visitType = visitType;
	}

	public Set<LabSample> getLabSamples() {
		return this.labSamples;
	}

	public void setLabSamples(Set<LabSample> labSamples) {
		this.labSamples = labSamples;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getAppointmentCardDate() {
		return appointmentCardDate;
	}

	public void setAppointmentCardDate(Date appointmentCardDate) {
		this.appointmentCardDate = appointmentCardDate;
	}

	public String getAppointmentCardNotes() {
		return appointmentCardNotes;
	}

	public void setAppointmentCardNotes(String appointmentCardNotes) {
		this.appointmentCardNotes = appointmentCardNotes;
	}

	public String getAppointmentCardTime() {
		return appointmentCardTime;
	}

	public void setAppointmentCardTime(String appointmentCardTime) {
		this.appointmentCardTime = appointmentCardTime;
	}

	public Set<OrderArtifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(Set<OrderArtifact> artifacts) {
		this.artifacts = artifacts;
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
		EmrVisit other = (EmrVisit) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmrVisit [rid=" + rid + "]";
	}
}