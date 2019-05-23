package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpCoverageDetailScope;
import com.optimiza.ehope.lis.lkp.model.LkpVisitType;

/**
 * InsCoverageDetail.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/23/2018
 **/
@Entity
@Table(name = "ins_coverage_detail")
@Audited
public class InsCoverageDetail extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "is_covered")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isCovered;

	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@Column(name = "need_authorization")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean needAuthorization;

	@Column(name = "percentage")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	private BigDecimal percentage;

	@Column(name = "max_amount")
	@Digits(integer = 15, fraction = 2)
	private BigDecimal maxAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scope_id")
	private LkpCoverageDetailScope lkpCoverageDetailScope;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id")
	private InsProviderPlan insProviderPlan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "visit_type_id")
	private LkpVisitType lkpVisitType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id")
	private BillClassification billClassification;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private BillMasterItem billMasterItem;

	public InsCoverageDetail() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public InsProviderPlan getInsProviderPlan() {
		return insProviderPlan;
	}

	public void setInsProviderPlan(InsProviderPlan insProviderPlan) {
		this.insProviderPlan = insProviderPlan;
	}

	public Boolean getIsCovered() {
		return isCovered;
	}

	public void setIsCovered(Boolean isCovered) {
		this.isCovered = isCovered;
	}

	public Boolean getNeedAuthorization() {
		return needAuthorization;
	}

	public void setNeedAuthorization(Boolean needAuthorization) {
		this.needAuthorization = needAuthorization;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public LkpCoverageDetailScope getLkpCoverageDetailScope() {
		return lkpCoverageDetailScope;
	}

	public void setLkpCoverageDetailScope(LkpCoverageDetailScope lkpCoverageDetailScope) {
		this.lkpCoverageDetailScope = lkpCoverageDetailScope;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public LkpVisitType getLkpVisitType() {
		return lkpVisitType;
	}

	public void setLkpVisitType(LkpVisitType lkpVisitType) {
		this.lkpVisitType = lkpVisitType;
	}

	public BillClassification getBillClassification() {
		return billClassification;
	}

	public void setBillClassification(BillClassification billClassification) {
		this.billClassification = billClassification;
	}

	public BillMasterItem getBillMasterItem() {
		return billMasterItem;
	}

	public void setBillMasterItem(BillMasterItem billMasterItem) {
		this.billMasterItem = billMasterItem;
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
		InsCoverageDetail other = (InsCoverageDetail) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InsCoverageDetail [rid=" + rid + "]";
	}

}