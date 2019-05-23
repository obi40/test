package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;

import com.optimiza.ehope.lis.model.EmrVisit;

public class DailyIncomeWrapper {

	EmrVisit emrVisit;
	BigDecimal totalDiscount;
	BigDecimal totalCash;
	BigDecimal totalCredit;
	BigDecimal totalNotPaid;
	BigDecimal totalOriginalPrice;

	public BigDecimal getTotalOriginalPrice() {
		return totalOriginalPrice;
	}

	public void setTotalOriginalPrice(BigDecimal totalOriginalPrice) {
		this.totalOriginalPrice = totalOriginalPrice;
	}

	public EmrVisit getEmrVisit() {
		return emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
	}

	public BigDecimal getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(BigDecimal totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public BigDecimal getTotalCash() {
		return totalCash;
	}

	public void setTotalCash(BigDecimal totalCash) {
		this.totalCash = totalCash;
	}

	public BigDecimal getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(BigDecimal totalCredit) {
		this.totalCredit = totalCredit;
	}

	public BigDecimal getTotalNotPaid() {
		return totalNotPaid;
	}

	public void setTotalNotPaid(BigDecimal totalNotPaid) {
		this.totalNotPaid = totalNotPaid;
	}

}
