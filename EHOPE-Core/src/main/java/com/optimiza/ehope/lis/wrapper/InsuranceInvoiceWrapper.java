package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;

import com.optimiza.ehope.lis.model.LabTestActual;

public class InsuranceInvoiceWrapper {

	LabTestActual labTestActual;
	BigDecimal fullCharge;
	BigDecimal coPayment;
	BigDecimal contractDiscount;
	BigDecimal claimedNetAmount;

	public LabTestActual getLabTestActual() {
		return labTestActual;
	}

	public void setLabTestActual(LabTestActual labTestActual) {
		this.labTestActual = labTestActual;
	}

	public BigDecimal getFullCharge() {
		return fullCharge;
	}

	public void setFullCharge(BigDecimal fullCharge) {
		this.fullCharge = fullCharge;
	}

	public BigDecimal getCoPayment() {
		return coPayment;
	}

	public void setCoPayment(BigDecimal coPayment) {
		this.coPayment = coPayment;
	}

	public BigDecimal getContractDiscount() {
		return contractDiscount;
	}

	public void setContractDiscount(BigDecimal contractDiscount) {
		this.contractDiscount = contractDiscount;
	}

	public BigDecimal getClaimedNetAmount() {
		return claimedNetAmount;
	}

	public void setClaimedNetAmount(BigDecimal claimedNetAmount) {
		this.claimedNetAmount = claimedNetAmount;
	}

}
