package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;

import com.optimiza.ehope.lis.lkp.model.LkpPaymentMethod;
import com.optimiza.ehope.lis.model.EmrVisit;

public class DailyCashWrapper {

	EmrVisit emrVisit;
	LkpPaymentMethod paymentMethod;
	BigDecimal totalRefund;
	BigDecimal totalDiscount;
	BigDecimal totalAmount;

	public DailyCashWrapper(EmrVisit emrVisit, LkpPaymentMethod paymentMethod, BigDecimal totalRefund, BigDecimal totalDiscount,
			BigDecimal totalAmount) {
		super();
		this.emrVisit = emrVisit;
		this.paymentMethod = paymentMethod;
		this.totalRefund = totalRefund;
		this.totalDiscount = totalDiscount;
		this.totalAmount = totalAmount;
	}

	public DailyCashWrapper() {
		super();
	}

	public EmrVisit getEmrVisit() {
		return emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
	}

	public BigDecimal getTotalRefund() {
		return totalRefund;
	}

	public void setTotalRefund(BigDecimal totalRefund) {
		this.totalRefund = totalRefund;
	}

	public BigDecimal getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(BigDecimal totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LkpPaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(LkpPaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}
