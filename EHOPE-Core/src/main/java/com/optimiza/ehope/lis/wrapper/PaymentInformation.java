package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.model.InsCoverageDetail;

public class PaymentInformation extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private BigDecimal charge;
	private BigDecimal percentage;//ins percentage
	private BigDecimal insCoverageResult;
	private BigDecimal insDeductionPercentage;
	private BigDecimal insDeductionResult;
	private BigDecimal groupPercentage;//test group percentage
	private BigDecimal groupAmount;//test group amount
	private BigDecimal groupResult;//test group deduction result
	private BigDecimal groupTotal;//test group total, to calculate percentage from groupAmount (UI stuff)
	private BigDecimal chargeAfterCoverage;
	private InsCoverageDetail insCoverageDetail;
	private BillMasterItem billMasterItem;
	private BillPricing billPricing;
	private String comment;
	private Boolean exceededMaxAmount = null;
	private Boolean isAuthorized = null;
	private Boolean isFetched = null;// to know if this bill master item inside this test didn't got fetched because it does not have a pricing that fit the requirements 

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((billMasterItem == null) ? 0 : billMasterItem.hashCode());
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
		PaymentInformation other = (PaymentInformation) obj;
		if (billMasterItem == null) {
			if (other.billMasterItem != null)
				return false;
		} else if (!billMasterItem.equals(other.billMasterItem))
			return false;
		return true;
	}

	public BigDecimal getGroupTotal() {
		return groupTotal;
	}

	public void setGroupTotal(BigDecimal groupTotal) {
		this.groupTotal = groupTotal;
	}

	public BigDecimal getChargeAfterCoverage() {
		return chargeAfterCoverage;
	}

	public void setChargeAfterCoverage(BigDecimal chargeAfterCoverage) {
		this.chargeAfterCoverage = chargeAfterCoverage;
	}

	public BigDecimal getInsDeductionPercentage() {
		return insDeductionPercentage;
	}

	public void setInsDeductionPercentage(BigDecimal insDeductionPercentage) {
		this.insDeductionPercentage = insDeductionPercentage;
	}

	@Override
	public String toString() {
		return "PaymentInformation [billMasterItem=" + billMasterItem + "]";
	}

	public BigDecimal getGroupPercentage() {
		return groupPercentage;
	}

	public void setGroupPercentage(BigDecimal groupPercentage) {
		this.groupPercentage = groupPercentage;
	}

	public Boolean getIsFetched() {
		return isFetched;
	}

	public void setIsFetched(Boolean isFetched) {
		this.isFetched = isFetched;
	}

	public Boolean getExceededMaxAmount() {
		return exceededMaxAmount;
	}

	public void setExceededMaxAmount(Boolean exceededMaxAmount) {
		this.exceededMaxAmount = exceededMaxAmount;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public BillPricing getBillPricing() {
		return billPricing;
	}

	public void setBillPricing(BillPricing billPricing) {
		this.billPricing = billPricing;
	}

	public BillMasterItem getBillMasterItem() {
		return billMasterItem;
	}

	public void setBillMasterItem(BillMasterItem billMasterItem) {
		this.billMasterItem = billMasterItem;
	}

	public BigDecimal getCharge() {
		return charge;
	}

	public void setCharge(BigDecimal charge) {
		this.charge = charge;
	}

	public InsCoverageDetail getInsCoverageDetail() {
		return insCoverageDetail;
	}

	public void setInsCoverageDetail(InsCoverageDetail insCoverageDetail) {
		this.insCoverageDetail = insCoverageDetail;
	}

	public Boolean getIsAuthorized() {
		return isAuthorized;
	}

	public void setIsAuthorized(Boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public BigDecimal getGroupAmount() {
		return groupAmount;
	}

	public void setGroupAmount(BigDecimal groupAmount) {
		this.groupAmount = groupAmount;
	}

	public BigDecimal getInsCoverageResult() {
		return insCoverageResult;
	}

	public void setInsCoverageResult(BigDecimal insCoverageResult) {
		this.insCoverageResult = insCoverageResult;
	}

	public BigDecimal getInsDeductionResult() {
		return insDeductionResult;
	}

	public void setInsDeductionResult(BigDecimal insDeductionResult) {
		this.insDeductionResult = insDeductionResult;
	}

	public BigDecimal getGroupResult() {
		return groupResult;
	}

	public void setGroupResult(BigDecimal groupResult) {
		this.groupResult = groupResult;
	}

}
