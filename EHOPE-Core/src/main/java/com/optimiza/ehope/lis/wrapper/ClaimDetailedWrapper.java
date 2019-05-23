package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;

import com.optimiza.ehope.lis.model.EmrVisit;

public class ClaimDetailedWrapper {

	EmrVisit emrVisit;
	BigDecimal fullCharge;
	BigDecimal coPayment;
	BigDecimal contractDiscount;
	BigDecimal claimAmount;
	BigDecimal claimedNetAmount;

	public EmrVisit getEmrVisit() {
		return emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
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

	public BigDecimal getClaimAmount() {
		return claimAmount;
	}

	public void setClaimAmount(BigDecimal claimAmount) {
		this.claimAmount = claimAmount;
	}

	public BigDecimal getClaimedNetAmount() {
		return claimedNetAmount;
	}

	public void setClaimedNetAmount(BigDecimal claimedNetAmount) {
		this.claimedNetAmount = claimedNetAmount;
	}

}
