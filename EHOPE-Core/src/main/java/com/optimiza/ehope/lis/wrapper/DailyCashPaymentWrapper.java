package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;

import com.optimiza.ehope.lis.model.EmrVisit;

public class DailyCashPaymentWrapper {

	EmrVisit emrVisit;
	BigDecimal totalAmount;

	public DailyCashPaymentWrapper(EmrVisit emrVisit, BigDecimal totalAmount) {
		super();
		this.emrVisit = emrVisit;
		this.totalAmount = totalAmount;
	}

	public DailyCashPaymentWrapper() {
		super();
	}

	public EmrVisit getEmrVisit() {
		return emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

}
