package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * BillChargeSlip.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Feb/19/2018
 * 
 **/
@Entity
@Table(name = "bill_charge_slip")
@Audited
public class BillChargeSlip extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	@Basic(optional = false)
	private Long rid;

	//Original price of test before any deductions
	@NotNull
	@Digits(integer = 15, fraction = 3)
	@Column(name = "original_price")
	private BigDecimal originalPrice;

	//Percentage that insurance or any level of insurances covered it, also profiles since they use insurance
	@Column(name = "ins_coverage_percentage")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	private BigDecimal percentage;

	// the amount of the ins_coverage_percentage that got covered
	@Column(name = "ins_coverage_result")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal insCoverageResult;

	// the deduction percentage that is on InsProvider to be deducted from the amount that the insurance will cover
	@Column(name = "ins_deduction_percentage")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	private BigDecimal insDeductionPercentage;

	// how much we deducted from the insurance coverage, net amount or gross amount
	@Column(name = "ins_deduction_result")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal insDeductionResult;

	// group coverage percentage
	@Column(name = "group_coverage_percentage")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	private BigDecimal groupDiscountPercentage;

	// group coverage amount
	@Column(name = "group_coverage_amount")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal groupCoverageAmount;

	// the amount that got covered by the group
	@Column(name = "group_coverage_result")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal groupCoverageResult;

	//the amount of patient share after group OR insurance coverages
	@NotNull
	@Column(name = "amount_after_coverage")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal amountAfterCoverage;

	// the general discount percentage on the total of the visit
	@Column(name = "general_discount_percentage")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	private BigDecimal generalDiscountPercentage;

	// the general discount amount on the total of the visit
	@Column(name = "general_discount_amount")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal generalDiscountAmount;

	// how much this charge slip cost  for the patient(final amount)
	@NotNull
	@Column(name = "amount")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal amount;

	// how much we got charged for sending this test to other lab
	@Column(name = "referral_amount")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal referralAmount;

	@NotNull
	@Column(name = "code")
	private String code;

	@NotNull
	@Column(name = "is_authorized")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isAuthorized;

	@NotNull
	@Column(name = "is_cancelled")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isCancelled;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coverage_detail_id")
	private InsCoverageDetail insCoverageDetail;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_actual_id")
	private LabTestActual labTestActual;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pricing_id")
	private BillPricing billPricing;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "master_item_id")
	private BillMasterItem billMasterItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id")
	private BillClassification billClassification;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private BillClassification parentClassification;

	@OneToMany(mappedBy = "billChargeSlip", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("billChargeSlip")
	private Set<BillPatientTransaction> billPatientTransactionList;

	@NotNull
	@Column(name = "is_exceed_max_amount")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isExceedMaxAmount;

	@Column(name = "comment")
	private String comment;

	@Transient
	@JsonProperty
	private BigDecimal tempAmount;

	@Transient
	@JsonProperty
	private BigDecimal groupTotal;

	public BillChargeSlip() {
	}

	public BigDecimal getGroupTotal() {
		return groupTotal;
	}

	public void setGroupTotal(BigDecimal groupTotal) {
		this.groupTotal = groupTotal;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getIsExceedMaxAmount() {
		return isExceedMaxAmount;
	}

	public void setIsExceedMaxAmount(Boolean isExceedMaxAmount) {
		this.isExceedMaxAmount = isExceedMaxAmount;
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public BigDecimal getInsCoverageResult() {
		return insCoverageResult;
	}

	public void setInsCoverageResult(BigDecimal insCoverageResult) {
		this.insCoverageResult = insCoverageResult;
	}

	public BigDecimal getGroupCoverageAmount() {
		return groupCoverageAmount;
	}

	public void setGroupCoverageAmount(BigDecimal groupCoverageAmount) {
		this.groupCoverageAmount = groupCoverageAmount;
	}

	public BigDecimal getGroupCoverageResult() {
		return groupCoverageResult;
	}

	public void setGroupCoverageResult(BigDecimal groupCoverageResult) {
		this.groupCoverageResult = groupCoverageResult;
	}

	public BigDecimal getAmountAfterCoverage() {
		return amountAfterCoverage;
	}

	public void setAmountAfterCoverage(BigDecimal amountAfterCoverage) {
		this.amountAfterCoverage = amountAfterCoverage;
	}

	public BigDecimal getGeneralDiscountAmount() {
		return generalDiscountAmount;
	}

	public void setGeneralDiscountAmount(BigDecimal generalDiscountAmount) {
		this.generalDiscountAmount = generalDiscountAmount;
	}

	public BigDecimal getInsDeductionResult() {
		return insDeductionResult;
	}

	public void setInsDeductionResult(BigDecimal insDeductionResult) {
		this.insDeductionResult = insDeductionResult;
	}

	public BigDecimal getReferralAmount() {
		return referralAmount;
	}

	public void setReferralAmount(BigDecimal referralAmount) {
		this.referralAmount = referralAmount;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public BigDecimal getTempAmount() {
		return tempAmount;
	}

	public void setTempAmount(BigDecimal tempAmount) {
		this.tempAmount = tempAmount;
	}

	public BigDecimal getInsDeductionPercentage() {
		return insDeductionPercentage;
	}

	public void setInsDeductionPercentage(BigDecimal insDeductionPercentage) {
		this.insDeductionPercentage = insDeductionPercentage;
	}

	public BigDecimal getGroupDiscountPercentage() {
		return groupDiscountPercentage;
	}

	public void setGroupDiscountPercentage(BigDecimal groupDiscountPercentage) {
		this.groupDiscountPercentage = groupDiscountPercentage;
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
		BillChargeSlip other = (BillChargeSlip) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BillChargeSlip [rid=" + rid + "]";
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Boolean getIsAuthorized() {
		return isAuthorized;
	}

	public void setIsAuthorized(Boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public InsCoverageDetail getInsCoverageDetail() {
		return insCoverageDetail;
	}

	public void setInsCoverageDetail(InsCoverageDetail insCoverageDetail) {
		this.insCoverageDetail = insCoverageDetail;
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

	public BillClassification getBillClassification() {
		return billClassification;
	}

	public void setBillClassification(BillClassification billClassification) {
		this.billClassification = billClassification;
	}

	public BillClassification getParentClassification() {
		return parentClassification;
	}

	public void setParentClassification(BillClassification parentClassification) {
		this.parentClassification = parentClassification;
	}

	public Set<BillPatientTransaction> getBillPatientTransactionList() {
		return billPatientTransactionList;
	}

	public void setBillPatientTransactionList(Set<BillPatientTransaction> billPatientTransactionList) {
		this.billPatientTransactionList = billPatientTransactionList;
	}

	public LabTestActual getLabTestActual() {
		return labTestActual;
	}

	public void setLabTestActual(LabTestActual labTestActual) {
		this.labTestActual = labTestActual;
	}

	public BigDecimal getGeneralDiscountPercentage() {
		return generalDiscountPercentage;
	}

	public void setGeneralDiscountPercentage(BigDecimal generalDiscountPercentage) {
		this.generalDiscountPercentage = generalDiscountPercentage;
	}

}