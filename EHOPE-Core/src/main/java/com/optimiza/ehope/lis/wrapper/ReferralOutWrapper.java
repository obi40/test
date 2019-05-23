package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;

import com.optimiza.ehope.lis.model.EmrVisit;

public class ReferralOutWrapper {

	private EmrVisit emrVisit;
	private BigDecimal originalPrices;
	private BigDecimal referralPrices;

	public EmrVisit getEmrVisit() {
		return emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
	}

	public BigDecimal getOriginalPrices() {
		return originalPrices;
	}

	public void setOriginalPrices(BigDecimal originalPrices) {
		this.originalPrices = originalPrices;
	}

	public BigDecimal getReferralPrices() {
		return referralPrices;
	}

	public void setReferralPrices(BigDecimal referralPrices) {
		this.referralPrices = referralPrices;
	}

}
