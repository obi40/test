package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;

import com.optimiza.core.common.data.model.TransField;

public class ClaimSummarizedWrapper {

	TransField companyName;
	BigDecimal fullCharge;
	BigDecimal coPayment;
	BigDecimal contractDiscount;
	BigDecimal claimedNetAmount;

	public TransField getCompanyName() {
		return companyName;
	}

	public void setCompanyName(TransField companyName) {
		this.companyName = companyName;
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
