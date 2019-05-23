package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.core.lkp.model.LkpCurrency;
import com.optimiza.ehope.lis.lkp.model.LkpAmountType;
import com.optimiza.ehope.lis.lkp.model.LkpPaymentMethod;
import com.optimiza.ehope.lis.lkp.model.LkpTransactionType;

/**
 * BillPatientTransaction.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Feb/19/2018
 **/
@Entity
@Table(name = "bill_patient_transaction")
@Audited
public class BillPatientTransaction extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	@Basic(optional = false)
	private Long rid;

	@Column(name = "amount")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal amount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "amount_type_id")
	private LkpAmountType lkpAmountType;

	@Column(name = "description")
	private String description;

	@NotNull
	@Column(name = "code")
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_method_id")
	private LkpPaymentMethod lkpPaymentMethod;

	@Digits(integer = 3, fraction = 3)
	@Column(name = "change_rate")
	private BigDecimal changeRate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_currency_id")
	private LkpCurrency lkpPaymentCurrency;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_type_id")
	private LkpTransactionType lkpTransactionType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "charge_slip_id")
	private BillChargeSlip billChargeSlip;

	@Transient
	@JsonSerialize
	private SecUser receivedBy;

	public BillPatientTransaction() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
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
		BillPatientTransaction other = (BillPatientTransaction) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BillPatientTransaction [rid=" + rid + "]";
	}

	public LkpAmountType getLkpAmountType() {
		return lkpAmountType;
	}

	public void setLkpAmountType(LkpAmountType lkpAmountType) {
		this.lkpAmountType = lkpAmountType;
	}

	public LkpTransactionType getLkpTransactionType() {
		return lkpTransactionType;
	}

	public void setLkpTransactionType(LkpTransactionType lkpTransactionType) {
		this.lkpTransactionType = lkpTransactionType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BillChargeSlip getBillChargeSlip() {
		return billChargeSlip;
	}

	public void setBillChargeSlip(BillChargeSlip billChargeSlip) {
		this.billChargeSlip = billChargeSlip;
	}

	public LkpPaymentMethod getLkpPaymentMethod() {
		return lkpPaymentMethod;
	}

	public void setLkpPaymentMethod(LkpPaymentMethod lkpPaymentMethod) {
		this.lkpPaymentMethod = lkpPaymentMethod;
	}

	public BigDecimal getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(BigDecimal changeRate) {
		this.changeRate = changeRate;
	}

	public LkpCurrency getLkpPaymentCurrency() {
		return lkpPaymentCurrency;
	}

	public void setLkpPaymentCurrency(LkpCurrency lkpPaymentCurrency) {
		this.lkpPaymentCurrency = lkpPaymentCurrency;
	}

	public SecUser getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(SecUser receivedBy) {
		this.receivedBy = receivedBy;
	}

}