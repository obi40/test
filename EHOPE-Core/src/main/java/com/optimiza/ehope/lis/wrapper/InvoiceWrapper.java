package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;
import java.util.List;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.ehope.lis.model.BillChargeSlip;
import com.optimiza.ehope.lis.model.BillPatientTransaction;
import com.optimiza.ehope.lis.model.Doctor;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.LabBranch;

public class InvoiceWrapper {

	private SecTenant tenant;
	private EmrVisit emrVisit;
	private EmrPatientInfo emrPatientInfo;
	private Doctor doctor;
	private LabBranch labBranch;
	private List<BillChargeSlip> bills;
	private BigDecimal generalDiscountAmount;

	private List<BillPatientTransaction> payments;
	private List<BillPatientTransaction> cancels;
	private List<BillPatientTransaction> recalculates;
	private List<BillPatientTransaction> refunds;

	private List<InsuranceInvoiceWrapper> insuranceTestsList;

	public List<InsuranceInvoiceWrapper> getInsuranceTestsList() {
		return insuranceTestsList;
	}

	public void setInsuranceTestsList(List<InsuranceInvoiceWrapper> insuranceTestsList) {
		this.insuranceTestsList = insuranceTestsList;
	}

	public List<BillPatientTransaction> getPayments() {
		return payments;
	}

	public SecTenant getTenant() {
		return tenant;
	}

	public void setTenant(SecTenant tenant) {
		this.tenant = tenant;
	}

	public void setPayments(List<BillPatientTransaction> payments) {
		this.payments = payments;
	}

	public List<BillPatientTransaction> getCancels() {
		return cancels;
	}

	public void setCancels(List<BillPatientTransaction> cancels) {
		this.cancels = cancels;
	}

	public List<BillPatientTransaction> getRecalculates() {
		return recalculates;
	}

	public void setRecalculates(List<BillPatientTransaction> recalculates) {
		this.recalculates = recalculates;
	}

	public List<BillPatientTransaction> getRefunds() {
		return refunds;
	}

	public void setRefunds(List<BillPatientTransaction> refunds) {
		this.refunds = refunds;
	}

	public EmrVisit getEmrVisit() {
		return emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
	}

	public EmrPatientInfo getEmrPatientInfo() {
		return emrPatientInfo;
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

	public LabBranch getLabBranch() {
		return labBranch;
	}

	public void setLabBranch(LabBranch labBranch) {
		this.labBranch = labBranch;
	}

	public List<BillChargeSlip> getBills() {
		return bills;
	}

	public void setBills(List<BillChargeSlip> bills) {
		this.bills = bills;
	}

	public BigDecimal getGeneralDiscountAmount() {
		return generalDiscountAmount;
	}

	public void setGeneralDiscountAmount(BigDecimal generalDiscountAmount) {
		this.generalDiscountAmount = generalDiscountAmount;
	}
}
